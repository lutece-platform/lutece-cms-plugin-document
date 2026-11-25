/*
 * Document modify form: moves the plugin-extend actions into the Extensions tab of the properties column.
 * Each action is a form of its own, rendered out of the document form (#documentExtendForms): its row goes to the tab
 * and its buttons submit the form left behind through their form attribute. The #extender anchor opens the tab.
 */
( ( ) => {
	const init = ( ) => {
		const source = document.getElementById( 'documentExtendForms' );
		const list = document.getElementById( 'documentExtendList' );
		if ( !source || !list ) {
			return;
		}
		source.querySelectorAll( 'form' ).forEach( ( form, index ) => {
			const row = form.querySelector( '.extend-action-menu' );
			if ( !row ) {
				return;
			}
			form.id = form.id || 'documentExtendAction' + index;
			row.querySelectorAll( 'button' ).forEach( ( button ) => button.setAttribute( 'form', form.id ) );
			const item = document.createElement( 'li' );
			item.append( row );
			list.append( item );
		} );
		if ( window.location.hash === '#extender' && window.bootstrap ) {
			const tab = document.getElementById( 'documentExtensions-tab' );
			tab && window.bootstrap.Tab.getOrCreateInstance( tab ).show( );
		}
	};

	if ( document.readyState === 'loading' ) {
		document.addEventListener( 'DOMContentLoaded', init );
	} else {
		init( );
	}
} )( );
