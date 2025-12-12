/**
 * Document Drag and Drop Module
 * Allows dragging documents (.document-row) and dropping them on the space tree
 * to move documents from one space to another.
 * Also allows dragging spaces in the tree to reorganize the hierarchy.
 */
const DocumentDragDrop = (function() {
    'use strict';

    // Configuration
    const config = {
        documentRowSelector: '.document-row',
        treeSelector: '#lutece-tree',
        treeNodeSelector: '#lutece-tree li',
        dropTargetClass: 'drop-target',
        draggingClass: 'dragging',
        dropHoverClass: 'drop-hover',
        dropValidClass: 'drop-valid',
        dropInvalidClass: 'drop-invalid',
        moveDocumentUrl: 'jsp/admin/plugins/document/DoMoveDocument.jsp',
        moveSpaceUrl: 'jsp/admin/plugins/document/DoMoveSpace.jsp',
        // Messages
        messages: {
            confirmMoveDocument: 'Voulez-vous déplacer le document "{title}" vers l\'espace "{space}" ?',
            confirmMoveSpace: 'Voulez-vous déplacer l\'espace "{title}" dans l\'espace "{space}" ?',
            moveSuccess: 'Déplacement effectué avec succès',
            moveError: 'Erreur lors du déplacement',
            invalidDrop: 'Impossible de déplacer ici',
            cannotMoveToSelf: 'Impossible de déplacer un espace dans lui-même',
            cannotMoveToChild: 'Impossible de déplacer un espace dans un de ses sous-espaces'
        }
    };

    // State
    let draggedElement = null;
    let draggedDocumentId = null;
    let draggedDocumentTitle = null;
    let draggedSpaceId = null;
    let draggedSpaceName = null;
    let dragType = null; // 'document' or 'space'
    let currentDropTarget = null;

    /**
     * Initialize the drag and drop functionality
     */
    function init() {
        const tree = document.querySelector(config.treeSelector);
        const documentRows = document.querySelectorAll(config.documentRowSelector);

        if (!tree) {
            console.warn('DocumentDragDrop: Tree not found');
            return;
        }

        // Add styles for drag and drop
        addStyles();

        // Initialize draggable documents
        if (documentRows.length > 0) {
            documentRows.forEach(row => {
                initDraggableDocument(row);
            });
        }

        // Initialize tree nodes (as drop targets AND draggable spaces)
        const treeNodes = document.querySelectorAll(config.treeNodeSelector);
        treeNodes.forEach(node => {
            initDropTarget(node);
            initDraggableSpace(node);
        });

        console.log('DocumentDragDrop: Initialized with', documentRows.length, 'documents and', treeNodes.length, 'spaces');
    }

    /**
     * Add CSS styles for drag and drop visual feedback
     */
    function addStyles() {
        if (document.getElementById('document-dragdrop-styles')) return;

        const styles = document.createElement('style');
        styles.id = 'document-dragdrop-styles';
        styles.textContent = `
            /* Draggable document rows */
            .document-row {
                cursor: grab;
                transition: opacity 0.2s, transform 0.2s, box-shadow 0.2s;
            }
            
            .document-row:hover {
                box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            }
            
            .document-row.dragging {
                opacity: 0.5;
                cursor: grabbing;
                transform: scale(0.98);
                box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            }
            
            /* Drop targets in tree */
            #lutece-tree li.drop-hover {
                background-color: rgba(var(--bs-primary-rgb, 13, 110, 253), 0.1);
                border-color: var(--bs-primary, #0d6efd);
                transform: scale(1.02);
            }
            
            #lutece-tree li.drop-valid {
                background-color: rgba(var(--bs-success-rgb, 25, 135, 84), 0.15);
                border-color: var(--bs-success, #198754);
            }
            
            #lutece-tree li.drop-invalid {
                background-color: rgba(var(--bs-danger-rgb, 220, 53, 69), 0.1);
                border-color: var(--bs-danger, #dc3545);
                cursor: not-allowed;
            }
            
            /* Drag ghost element */
            .drag-ghost {
                position: fixed;
                pointer-events: none;
                z-index: 9999;
                padding: 8px 16px;
                background: var(--bs-primary, #0d6efd);
                color: white;
                border-radius: 4px;
                font-size: 14px;
                font-weight: 500;
                box-shadow: 0 4px 12px rgba(0,0,0,0.3);
                max-width: 300px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            
            .drag-ghost::before {
                content: '📄 ';
            }
            
            .drag-ghost.space-ghost::before {
                content: '📁 ';
            }
            
            /* Draggable space nodes */
           
            
            /* Drop position indicators for spaces */
            #lutece-tree li.drop-before::before {
                content: '';
                display: block;
                height: 3px;
                background: var(--bs-primary, #0d6efd);
                margin-bottom: 2px;
                border-radius: 2px;
            }
            
            #lutece-tree li.drop-after::after {
                content: '';
                display: block;
                height: 3px;
                background: var(--bs-primary, #0d6efd);
                margin-top: 2px;
                border-radius: 2px;
            }
            
            #lutece-tree li.drop-inside {
                background-color: rgba(var(--bs-success-rgb, 25, 135, 84), 0.2);
                border: 2px dashed var(--bs-success, #198754);
            }
            
            /* Drop indicator icon */
            #lutece-tree li.drop-hover > a::after {
                content: ' ← Déposer ici';
                font-size: 12px;
                color: var(--bs-primary, #0d6efd);
                font-weight: bold;
            }
            
            #lutece-tree li.drop-inside > a::after {
                content: ' ← Déplacer dans cet espace';
                font-size: 12px;
                color: var(--bs-success, #198754);
                font-weight: bold;
            }
            
            /* Animation for successful drop */
            @keyframes dropSuccess {
                0% { background-color: rgba(25, 135, 84, 0.3); }
                100% { background-color: transparent; }
            }
            
            .drop-success {
                animation: dropSuccess 0.5s ease-out;
            }
        `;
        document.head.appendChild(styles);
    }

    /**
     * Initialize a document row as draggable
     * @param {HTMLElement} row 
     */
    function initDraggableDocument(row) {
        row.setAttribute('draggable', 'true');
        row.addEventListener('dragstart', handleDocumentDragStart);
        row.addEventListener('dragend', handleDragEnd);
    }

    /**
     * Initialize a space node as draggable
     * @param {HTMLElement} node 
     */
    function initDraggableSpace(node) {
        // Make the node draggable
        const link = node.querySelector(':scope > a');
        if (link) {
            node.setAttribute('draggable', 'true');
            node.addEventListener('dragstart', handleSpaceDragStart);
            node.addEventListener('dragend', handleDragEnd);
        }
    }

    /**
     * Initialize a tree node as drop target
     * @param {HTMLElement} node 
     */
    function initDropTarget(node) {
        node.addEventListener('dragover', handleDragOver);
        node.addEventListener('dragenter', handleDragEnter);
        node.addEventListener('dragleave', handleDragLeave);
        node.addEventListener('drop', handleDrop);
    }

    /**
     * Handle document drag start event
     * @param {DragEvent} event 
     */
    function handleDocumentDragStart(event) {
        draggedElement = event.currentTarget;
        dragType = 'document';
        
        // Extract document info from data attribute or checkbox
        const dataDocument = draggedElement.getAttribute('data-document');
        if (dataDocument) {
            const parts = dataDocument.split(' ');
            draggedDocumentId = parts[0];
            draggedDocumentTitle = parts.slice(1).join(' ');
        } else {
            // Try to get from checkbox value
            const checkbox = draggedElement.querySelector('input[name="document_selection"]');
            if (checkbox) {
                draggedDocumentId = checkbox.value;
            }
            // Try to get title from strong element
            const titleEl = draggedElement.querySelector('strong');
            if (titleEl) {
                draggedDocumentTitle = titleEl.textContent.trim();
            }
        }

        // Set drag data
        event.dataTransfer.effectAllowed = 'move';
        event.dataTransfer.setData('text/plain', draggedDocumentId);
        event.dataTransfer.setData('application/json', JSON.stringify({
            type: 'document',
            id: draggedDocumentId,
            title: draggedDocumentTitle
        }));

        // Add dragging class
        draggedElement.classList.add(config.draggingClass);

        // Create custom drag image
        const ghost = createDragGhost(draggedDocumentTitle || `Document ${draggedDocumentId}`, 'document');
        document.body.appendChild(ghost);
        event.dataTransfer.setDragImage(ghost, 10, 10);
        
        // Remove ghost after a short delay
        setTimeout(() => ghost.remove(), 0);

        console.log('DragStart: Document', draggedDocumentId, draggedDocumentTitle);
    }

    /**
     * Handle space drag start event
     * @param {DragEvent} event 
     */
    function handleSpaceDragStart(event) {
        // Prevent drag if clicking on expand/collapse button
        if (event.target.closest('.lutece-tree-toggle')) {
            event.preventDefault();
            return;
        }

        draggedElement = event.currentTarget;
        dragType = 'space';
        
        // Get space ID and name
        draggedSpaceId = getSpaceIdFromNode(draggedElement);
        draggedSpaceName = getSpaceNameFromNode(draggedElement);

        if (!draggedSpaceId) {
            event.preventDefault();
            return;
        }

        // Set drag data
        event.dataTransfer.effectAllowed = 'move';
        event.dataTransfer.setData('text/plain', draggedSpaceId);
        event.dataTransfer.setData('application/json', JSON.stringify({
            type: 'space',
            id: draggedSpaceId,
            name: draggedSpaceName
        }));

        // Add dragging class
        draggedElement.classList.add(config.draggingClass);

        // Create custom drag image
        const ghost = createDragGhost(draggedSpaceName || `Espace ${draggedSpaceId}`, 'space');
        document.body.appendChild(ghost);
        event.dataTransfer.setDragImage(ghost, 10, 10);
        
        // Remove ghost after a short delay
        setTimeout(() => ghost.remove(), 0);

        console.log('DragStart: Space', draggedSpaceId, draggedSpaceName);
    }

    /**
     * Handle drag end event
     * @param {DragEvent} event 
     */
    function handleDragEnd(event) {
        // Remove dragging class
        if (draggedElement) {
            draggedElement.classList.remove(config.draggingClass);
        }

        // Clean up all drop hover states
        document.querySelectorAll(`.${config.dropHoverClass}, .${config.dropValidClass}, .${config.dropInvalidClass}, .drop-inside`)
            .forEach(el => {
                el.classList.remove(config.dropHoverClass, config.dropValidClass, config.dropInvalidClass, 'drop-inside');
            });

        // Reset state
        draggedElement = null;
        draggedDocumentId = null;
        draggedDocumentTitle = null;
        draggedSpaceId = null;
        draggedSpaceName = null;
        dragType = null;
        currentDropTarget = null;
    }

    /**
     * Handle drag over event
     * @param {DragEvent} event 
     */
    function handleDragOver(event) {
        event.preventDefault();
        event.dataTransfer.dropEffect = 'move';
    }

    /**
     * Handle drag enter event
     * @param {DragEvent} event 
     */
    function handleDragEnter(event) {
        event.preventDefault();
        
        const target = event.currentTarget;
        
        // If dragging a space, check if this is a valid drop target
        if (dragType === 'space' && draggedSpaceId) {
            const targetSpaceId = getSpaceIdFromNode(target);
            
            // Cannot drop on self
            if (targetSpaceId === draggedSpaceId) {
                target.classList.add(config.dropInvalidClass);
                return;
            }
            
            // Find the dragged space element and check if target is a child
            const draggedSpaceElement = findSpaceNodeById(draggedSpaceId);
            if (draggedSpaceElement && isChildOf(target, draggedSpaceElement)) {
                target.classList.add(config.dropInvalidClass);
                return;
            }
        }
        
        // Remove hover from previous target
        if (currentDropTarget && currentDropTarget !== target) {
            currentDropTarget.classList.remove(config.dropHoverClass, config.dropValidClass, 'drop-inside');
        }

        currentDropTarget = target;
        
        // Add appropriate hover class
        if (dragType === 'space') {
            target.classList.add('drop-inside');
        } else {
            target.classList.add(config.dropHoverClass);
        }

        // Check if this is a valid drop target
        const spaceId = getSpaceIdFromNode(target);
        if (spaceId) {
            target.classList.add(config.dropValidClass);
        }
    }

    /**
     * Handle drag leave event
     * @param {DragEvent} event 
     */
    function handleDragLeave(event) {
        const target = event.currentTarget;
        const relatedTarget = event.relatedTarget;

        // Only remove hover if we're actually leaving (not entering a child)
        if (!target.contains(relatedTarget)) {
            target.classList.remove(config.dropHoverClass, config.dropValidClass, config.dropInvalidClass, 'drop-inside');
        }
    }

    /**
     * Handle drop event
     * @param {DragEvent} event 
     */
    function handleDrop(event) {
        event.preventDefault();
        event.stopPropagation();

        const target = event.currentTarget;
        target.classList.remove(config.dropHoverClass, config.dropValidClass, config.dropInvalidClass, 'drop-inside');

        // Get target space info
        const targetSpaceId = getSpaceIdFromNode(target);
        const targetSpaceName = getSpaceNameFromNode(target);

        if (!targetSpaceId) {
            console.warn('Drop: Invalid target space');
            showToast(config.messages.invalidDrop, 'warning');
            return;
        }

        // Get drag data
        let dragData = null;
        try {
            const jsonData = event.dataTransfer.getData('application/json');
            if (jsonData) {
                dragData = JSON.parse(jsonData);
            }
        } catch (e) {
            // Fallback to text data
            dragData = {
                type: dragType,
                id: event.dataTransfer.getData('text/plain')
            };
        }

        if (!dragData || !dragData.id) {
            console.warn('Drop: Invalid drag data');
            showToast(config.messages.invalidDrop, 'warning');
            return;
        }

        // Handle drop based on type
        if (dragData.type === 'document') {
            handleDocumentDrop(dragData, targetSpaceId, targetSpaceName, target);
        } else if (dragData.type === 'space') {
            handleSpaceDrop(dragData, targetSpaceId, targetSpaceName, target);
        }
    }

    /**
     * Handle document drop
     * @param {Object} dragData 
     * @param {string} targetSpaceId 
     * @param {string} targetSpaceName 
     * @param {HTMLElement} targetNode 
     */
    function handleDocumentDrop(dragData, targetSpaceId, targetSpaceName, targetNode) {
        console.log('Drop: Moving document', dragData.id, 'to space', targetSpaceId);

        // Confirm the move
        const confirmMessage = config.messages.confirmMoveDocument
            .replace('{title}', dragData.title || dragData.id)
            .replace('{space}', targetSpaceName || targetSpaceId);

        if (confirm(confirmMessage)) {
            moveDocument(dragData.id, targetSpaceId, targetNode);
        }
    }

    /**
     * Handle space drop
     * @param {Object} dragData 
     * @param {string} targetSpaceId 
     * @param {string} targetSpaceName 
     * @param {HTMLElement} targetNode 
     */
    function handleSpaceDrop(dragData, targetSpaceId, targetSpaceName, targetNode) {
        // Cannot move space to itself
        if (dragData.id === targetSpaceId) {
            showToast(config.messages.cannotMoveToSelf, 'warning');
            return;
        }

        // Find the dragged space element by its ID
        const draggedSpaceElement = findSpaceNodeById(dragData.id);
        
        // Cannot move space to one of its children
        if (draggedSpaceElement && isChildOf(draggedSpaceElement, targetNode)) {
            showToast(config.messages.cannotMoveToChild, 'warning');
            return;
        }

        console.log('Drop: Moving space', dragData.id, 'to space', targetSpaceId);

        // Confirm the move
        const confirmMessage = config.messages.confirmMoveSpace
            .replace('{title}', dragData.name || dragData.id)
            .replace('{space}', targetSpaceName || targetSpaceId);

        if (confirm(confirmMessage)) {
            moveSpace(dragData.id, targetSpaceId, targetNode);
        }
    }

    /**
     * Check if potentialChild is a child of potentialParent
     * @param {HTMLElement} potentialChild 
     * @param {HTMLElement} potentialParent 
     * @returns {boolean}
     */
    function isChildOf(potentialChild, potentialParent) {
        if (!potentialParent || !potentialChild) return false;
        return potentialParent.contains(potentialChild) && potentialParent !== potentialChild;
    }

    /**
     * Find a space node in the tree by its ID
     * @param {string} spaceId 
     * @returns {HTMLElement|null}
     */
    function findSpaceNodeById(spaceId) {
        if (!spaceId) return null;
        
        // Try by node ID first
        const nodeById = document.getElementById(`node-${spaceId}`);
        if (nodeById) return nodeById;
        
        // Search in tree nodes
        const treeNodes = document.querySelectorAll(config.treeNodeSelector);
        for (const node of treeNodes) {
            const nodeSpaceId = getSpaceIdFromNode(node);
            if (nodeSpaceId === spaceId) {
                return node;
            }
        }
        
        return null;
    }

    /**
     * Get space ID from tree node
     * @param {HTMLElement} node 
     * @returns {string|null}
     */
    function getSpaceIdFromNode(node) {
        // Try to get from node ID (format: node-{spaceId})
        const nodeId = node.id;
        if (nodeId && nodeId.startsWith('node-')) {
            return nodeId.replace('node-', '');
        }

        // Try to get from link href
        const link = node.querySelector('a[href*="id_space_filter="]');
        if (link) {
            const href = link.getAttribute('href');
            const match = href.match(/id_space_filter=(\d+)/);
            if (match) {
                return match[1];
            }
        }

        // Try to get from link href (ManageDocuments)
        const linkAlt = node.querySelector('a[href*="id_space="]');
        if (linkAlt) {
            const href = linkAlt.getAttribute('href');
            const match = href.match(/id_space=(\d+)/);
            if (match) {
                return match[1];
            }
        }

        return null;
    }

    /**
     * Get space name from tree node
     * @param {HTMLElement} node 
     * @returns {string}
     */
    function getSpaceNameFromNode(node) {
        const link = node.querySelector('a');
        if (link) {
            return link.textContent.trim();
        }
        return '';
    }

    /**
     * Move document to target space
     * @param {string} documentId 
     * @param {string} targetSpaceId 
     * @param {HTMLElement} targetNode 
     */
    function moveDocument(documentId, targetSpaceId, targetNode) {
        // Build the URL
        const url = `${config.moveDocumentUrl}?id_document=${documentId}&browser_selected_space_id=${targetSpaceId}`;

        // Show loading state
        targetNode.classList.add('drop-valid');

        // Redirect to move document
        window.location.href = url;
    }

    /**
     * Move space to target space (as child)
     * @param {string} spaceId 
     * @param {string} targetSpaceId 
     * @param {HTMLElement} targetNode 
     */
    function moveSpace(spaceId, targetSpaceId, targetNode) {
        // Build the URL
        const url = `${config.moveSpaceUrl}?id_space=${spaceId}&browser_selected_space_id=${targetSpaceId}`;

        // Show loading state
        targetNode.classList.add('drop-valid');

        // Redirect to move space
        window.location.href = url;
    }

    /**
     * Create a custom drag ghost element
     * @param {string} title 
     * @param {string} type - 'document' or 'space'
     * @returns {HTMLElement}
     */
    function createDragGhost(title, type = 'document') {
        const ghost = document.createElement('div');
        ghost.className = 'drag-ghost' + (type === 'space' ? ' space-ghost' : '');
        ghost.textContent = title;
        ghost.style.position = 'absolute';
        ghost.style.top = '-1000px';
        ghost.style.left = '-1000px';
        return ghost;
    }

    /**
     * Show a toast notification
     * @param {string} message 
     * @param {string} type - 'success', 'danger', 'warning', 'info'
     */
    function showToast(message, type = 'info') {
        // Check if Bootstrap toast is available
        if (typeof bootstrap !== 'undefined' && bootstrap.Toast) {
            // Create toast element
            const toastEl = document.createElement('div');
            toastEl.className = `toast align-items-center text-white bg-${type} border-0`;
            toastEl.setAttribute('role', 'alert');
            toastEl.setAttribute('aria-live', 'assertive');
            toastEl.setAttribute('aria-atomic', 'true');
            toastEl.innerHTML = `
                <div class="d-flex">
                    <div class="toast-body">${message}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            `;

            // Add to container or create one
            let container = document.querySelector('.toast-container');
            if (!container) {
                container = document.createElement('div');
                container.className = 'toast-container position-fixed top-0 end-0 p-3';
                container.style.zIndex = '9999';
                document.body.appendChild(container);
            }
            container.appendChild(toastEl);

            const toast = new bootstrap.Toast(toastEl);
            toast.show();

            toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
        } else {
            // Fallback to alert
            alert(message);
        }
    }

    /**
     * Refresh drop targets (useful after tree updates)
     */
    function refresh() {
        const treeNodes = document.querySelectorAll(config.treeNodeSelector);
        treeNodes.forEach(node => {
            // Remove existing listeners by cloning
            const newNode = node.cloneNode(true);
            node.parentNode.replaceChild(newNode, node);
            initDropTarget(newNode);
            initDraggableSpace(newNode);
        });
    }

    /**
     * Destroy the drag and drop functionality
     */
    function destroy() {
        const documentRows = document.querySelectorAll(config.documentRowSelector);
        documentRows.forEach(row => {
            row.removeAttribute('draggable');
            row.classList.remove(config.draggingClass);
        });

        const treeNodes = document.querySelectorAll(config.treeNodeSelector);
        treeNodes.forEach(node => {
            node.removeAttribute('draggable');
            node.classList.remove(config.dropHoverClass, config.dropValidClass, config.dropInvalidClass, 'drop-inside');
        });

        // Remove styles
        const styles = document.getElementById('document-dragdrop-styles');
        if (styles) styles.remove();
    }

    // Public API
    return {
        init,
        refresh,
        destroy,
        config
    };
})();

// Auto-initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Small delay to ensure tree is rendered
    setTimeout(() => {
        DocumentDragDrop.init();
    }, 100);
});

// Export for module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = DocumentDragDrop;
}
