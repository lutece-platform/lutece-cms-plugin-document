/*
 * Document create and modify forms: hides or shows the properties column (#documentPropertiesTabs),
 * so the document column can take the full width. The choice is kept in the browser for the next forms.
 */
( ( ) => {
	const storageKey = 'document.propertiesHidden';
	const hiddenClass = 'document-properties-hidden';

	const init = ( ) => {
		const layout = document.querySelector( '.document-form-layout' );
		if ( !layout ) {
			return;
		}
		const hideButton = layout.querySelector( '.document-properties-hide' );
		const showButton = layout.querySelector( '.document-properties-show' );

		const setHidden = ( hidden, moveFocus ) => {
			layout.classList.toggle( hiddenClass, hidden );
			[ hideButton, showButton ].forEach( ( button ) => button && button.setAttribute( 'aria-expanded', String( !hidden ) ) );
			try {
				localStorage.setItem( storageKey, String( hidden ) );
			} catch ( e ) {
				// Storage unavailable (private window, blocked site data): the choice lasts for this page only
			}
			// The button that was clicked disappears: the focus goes to the one that does the opposite
			if ( moveFocus ) {
				( hidden ? showButton : hideButton )?.focus( );
			}
		};

		let stored = null;
		try {
			stored = localStorage.getItem( storageKey );
		} catch ( e ) {
			stored = null;
		}
		setHidden( stored === 'true', false );
		hideButton?.addEventListener( 'click', ( ) => setHidden( true, true ) );
		showButton?.addEventListener( 'click', ( ) => setHidden( false, true ) );
	};

	if ( document.readyState === 'loading' ) {
		document.addEventListener( 'DOMContentLoaded', init );
	} else {
		init( );
	}
} )( );
