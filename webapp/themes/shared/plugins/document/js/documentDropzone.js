/*
 * Drop zone of the file attributes of the document forms (attributes/file_dropzone.html).
 * The file is not sent on its own: a dropped file is put in the file input of the zone, and the form sends it on submit.
 * A 'change' event is fired on the input after a drop or a clear, so the scripts of the attribute templates react as to a selection.
 * The script may be loaded once per file attribute: each zone is initialised once.
 */
( ( ) => {
	const formatSize = ( bytes ) => {
		const units = [ 'B', 'KB', 'MB', 'GB' ];
		let size = bytes;
		let unit = 0;
		while ( size >= 1024 && unit < units.length - 1 ) { size /= 1024; unit++; }
		return ( unit === 0 ? size : size.toFixed( 1 ) ) + ' ' + units[ unit ];
	};

	const initZone = ( zone ) => {
		if ( zone.dataset.dropzoneReady ) {
			return;
		}
		zone.dataset.dropzoneReady = 'true';
		const input = zone.querySelector( 'input[type="file"]' );
		const fileName = zone.querySelector( '.document-dropzone-file' );
		const clear = zone.querySelector( '.document-dropzone-clear' );

		// The card that shows the chosen file, when the zone declares one (data-preview)
		const card = zone.dataset.preview ? document.getElementById( zone.dataset.preview ) : null;
		let objectUrl = null;

		const showPreview = ( file ) => {
			if ( !card ) {
				return;
			}
			const media = card.querySelector( '.document-file-new-media' );
			if ( objectUrl ) {
				URL.revokeObjectURL( objectUrl );
				objectUrl = null;
			}
			media.replaceChildren( );
			card.classList.toggle( 'd-none', !file );
			if ( !file ) {
				return;
			}
			card.querySelector( '.document-file-new-name' ).textContent = file.name;
			card.querySelector( '.document-file-new-meta' ).textContent = [ file.type, formatSize( file.size ) ].filter( Boolean ).join( ' · ' );
			if ( file.type.startsWith( 'image/' ) || file.type === 'application/pdf' ) {
				objectUrl = URL.createObjectURL( file );
			}
			let element;
			if ( file.type.startsWith( 'image/' ) ) {
				element = document.createElement( 'img' );
				element.src = objectUrl;
				element.alt = file.name;
				element.className = 'mw-100 mh-100';
				element.style.objectFit = 'contain';
			} else if ( file.type === 'application/pdf' ) {
				// The first page of the PDF, without the toolbar of the browser viewer; the icon is shown if the browser cannot display it
				element = document.createElement( 'object' );
				element.data = objectUrl + '#toolbar=0&navpanes=0&view=FitH';
				element.type = 'application/pdf';
				element.className = 'w-100 h-100';
				element.setAttribute( 'aria-label', file.name );
				const fallback = document.createElement( 'img' );
				fallback.src = card.dataset.iconPdf;
				fallback.alt = '';
				fallback.style.height = '4rem';
				element.append( fallback );
			} else {
				element = document.createElement( 'img' );
				element.src = card.dataset.iconFile;
				element.alt = '';
				element.style.height = '4rem';
			}
			media.append( element );
		};

		const showSelection = ( ) => {
			const file = input.files[ 0 ];
			zone.classList.toggle( 'has-file', !!file );
			fileName.textContent = file ? file.name + ' · ' + formatSize( file.size ) : '';
			showPreview( file );
		};

		// The accept attribute only filters the file chooser: a dropped file is checked here against it
		const accepted = ( file ) => !input.accept || input.accept.split( ',' ).map( ( type ) => type.trim( ).toLowerCase( ) ).some( ( type ) =>
			type.startsWith( '.' ) ? file.name.toLowerCase( ).endsWith( type )
				: type.endsWith( '/*' ) ? file.type.startsWith( type.slice( 0, -1 ) ) : file.type === type );

		const setFiles = ( files ) => {
			const kept = Array.from( files ).filter( accepted );
			if ( kept.length === 0 ) {
				return;
			}
			const transfer = new DataTransfer( );
			kept.slice( 0, input.multiple ? kept.length : 1 ).forEach( ( file ) => transfer.items.add( file ) );
			input.files = transfer.files;
			input.dispatchEvent( new Event( 'change', { bubbles: true } ) );
		};

		[ 'dragenter', 'dragover' ].forEach( ( type ) => zone.addEventListener( type, ( event ) => {
			event.preventDefault( );
			zone.classList.add( 'hover' );
		} ) );
		[ 'dragleave', 'dragend' ].forEach( ( type ) => zone.addEventListener( type, ( event ) => {
			if ( !zone.contains( event.relatedTarget ) ) {
				zone.classList.remove( 'hover' );
			}
		} ) );
		zone.addEventListener( 'drop', ( event ) => {
			event.preventDefault( );
			zone.classList.remove( 'hover' );
			if ( event.dataTransfer.files.length > 0 ) {
				setFiles( event.dataTransfer.files );
			}
		} );
		clear.addEventListener( 'click', ( ) => {
			input.value = '';
			input.dispatchEvent( new Event( 'change', { bubbles: true } ) );
			input.focus( );
		} );
		input.addEventListener( 'change', showSelection );
		showSelection( );
	};

	const initAll = ( ) => document.querySelectorAll( '.document-dropzone' ).forEach( initZone );
	if ( document.readyState === 'loading' ) {
		document.addEventListener( 'DOMContentLoaded', initAll );
	} else {
		initAll( );
	}
} )( );
