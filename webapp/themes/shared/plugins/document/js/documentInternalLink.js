/**
 * Internal link attributes of the document forms (create_internal_link.html, modify_internal_link.html).
 * The buttons carry data-document-internal-link ( service, preview or clear ) and data-code, the code of the
 * attribute: the textarea of the link has this id, its preview the id show_preview_<code>.
 * Loaded once per attribute: the listener is registered once.
 */
( function( ) {
    'use strict';

    if ( window.luteceDocumentInternalLink ) {
        return;
    }
    window.luteceDocumentInternalLink = true;

    const INSERT_SERVICES_URL = 'jsp/admin/insert/GetAvailableInsertServices.jsp';
    const SCHEMES_ALLOWED = [ 'http:', 'https:', 'mailto:' ];

    /**
     * Shows the links of the HTML held by the field: the HTML is parsed in an inert document, and only the text and
     * the address of its links are copied, an address with another scheme than http, https or mailto left out.
     * @param {HTMLElement} field the textarea of the link
     * @param {HTMLElement} preview the element showing the link
     */
    function showPreview( field, preview ) {
        preview.textContent = '';
        const parsed = new DOMParser( ).parseFromString( field.value, 'text/html' );
        const links = parsed.body.querySelectorAll( 'a[href]' );
        if ( links.length === 0 ) {
            preview.textContent = parsed.body.textContent;
            return;
        }
        links.forEach( function( source ) {
            const link = document.createElement( 'a' );
            try {
                const url = new URL( source.getAttribute( 'href' ), document.baseURI );
                if ( SCHEMES_ALLOWED.includes( url.protocol ) ) {
                    link.setAttribute( 'href', url.href );
                }
            } catch ( e ) {
                // an address that does not parse is left out
            }
            link.textContent = source.textContent;
            preview.appendChild( link );
        } );
    }

    document.addEventListener( 'click', function( event ) {
        const button = event.target.closest( '[data-document-internal-link]' );
        if ( !button ) {
            return;
        }
        event.preventDefault( );
        const code = button.dataset.code;
        const field = document.getElementById( code );
        const preview = document.getElementById( 'show_preview_' + code );
        if ( !field || !preview ) {
            return;
        }
        switch ( button.dataset.documentInternalLink ) {
            case 'service': {
                preview.textContent = '';
                const url = INSERT_SERVICES_URL + '?input=' + encodeURIComponent( code ) + '&selected_text=';
                const popup = window.open( url, 'linkService', 'toolbar=no, scrollbars=yes, status=yes, location=no, directories=no, menubar=no, width=550, height=450' );
                if ( popup ) {
                    popup.focus( );
                }
                break;
            }
            case 'preview':
                showPreview( field, preview );
                break;
            case 'clear':
                field.value = '';
                preview.textContent = '';
                break;
            default:
                break;
        }
    } );
} )( );
