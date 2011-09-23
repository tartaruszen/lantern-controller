package org.lantern;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lantern.data.Dao;

import com.google.appengine.api.xmpp.Presence;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

@SuppressWarnings("serial")
public class XmppAvailableServlet extends HttpServlet {
    
    //private final Logger log = Logger.getLogger(getClass().getName());
    
    @Override
    public void doPost(final HttpServletRequest req, 
        final HttpServletResponse res) throws IOException {
        final XMPPService service = XMPPServiceFactory.getXMPPService();
        final Presence presence = service.parsePresence(req);
        final boolean available = presence.isAvailable();
        final String id = presence.getFromJid().getId();
        System.out.println("XmppAvailableServlet::Got presence "+available+" for "+id);
        
        final String stanza = presence.getStanza();
        System.out.println("Stanza: "+stanza);
        //System.out.println("Status: "+presence.getStatus());
        if (LanternControllerUtils.isLantern(id)) {
            final Dao dao = new Dao();
            // The following will delete the instance if it's not available,
            // updating all counters.
            dao.setInstanceAvailable(id, available);
            
        } else {
            System.out.println("XmppAvailableServlet::Not a Lantern ID: "+id);
        }
    }
}