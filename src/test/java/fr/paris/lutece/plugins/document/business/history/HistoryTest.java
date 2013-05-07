/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.document.business.history;

import fr.paris.lutece.plugins.document.business.history.HistoryEvent;
import fr.paris.lutece.plugins.document.business.history.HistoryEventHome;
import fr.paris.lutece.test.LuteceTestCase;

import java.sql.Timestamp;

import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HistoryTest extends LuteceTestCase
{
    private static final int DOCUMENTID1 = -1;
    private final static Timestamp EVENTDATE1 = new Timestamp( new Date(  ).getTime(  ) );
    private final static String EVENTUSER1 = "JUnit EventUser 1";
    private final static String EVENTMESSAGEKEY1 = "JUnit EventMessageKey 1";
    private final static String DOCUMENTSTATEKEY1 = "JUnit DocumentStateKey 1";
    private final static String DOCUMENTSPACE1 = "JUnit DocumentSpace 1";

    public void testBusiness(  )
    {
        // Initialize an event
        HistoryEvent event = new HistoryEvent(  );
        event.setIdDocument( DOCUMENTID1 );
        event.setDate( EVENTDATE1 );
        event.setEventUser( EVENTUSER1 );
        event.setEventMessageKey( EVENTMESSAGEKEY1 );
        event.setDocumentStateKey( DOCUMENTSTATEKEY1 );
        event.setSpace( DOCUMENTSPACE1 );

        // Create test
        HistoryEventHome.create( event );

        List<HistoryEvent> listEvents = HistoryEventHome.findByDocument( event.getIdDocument(  ), Locale.FRENCH );
        HistoryEvent eventStored = listEvents.get( listEvents.size(  ) - 1 );
        assertTrue( ( eventStored.getDate(  ).getTime(  ) - event.getDate(  ).getTime(  ) ) < 1000 );
        assertEquals( eventStored.getEventUser(  ), event.getEventUser(  ) );
        assertEquals( eventStored.getEventMessageKey(  ), event.getEventMessageKey(  ) );
        assertEquals( eventStored.getDocumentStateKey(  ), event.getDocumentStateKey(  ) );
        assertEquals( eventStored.getSpace(  ), event.getSpace(  ) );
    }
}
