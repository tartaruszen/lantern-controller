package org.lantern;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lantern.data.Dao;
import org.lantern.data.LanternUser;


@SuppressWarnings("serial")
public class ProcessDonation extends HttpServlet {

    private static final transient Logger log = Logger
            .getLogger(ProcessDonation.class.getName());

    // In US dollar cents.
    private static int LAUNCH_UPFRONT_COST = 2000;

    //XXX: For testing; disable when deploying to the real controller!
    /*@Override
    public void doGet(final HttpServletRequest request,
                      final HttpServletResponse response) {
        doPost(request, response);
    }*/

    @Override
    public void doPost(final HttpServletRequest request,
                       final HttpServletResponse response) {
        final String id = request.getParameter(
                LanternControllerConstants.ID_KEY);
        final String email = request.getParameter(
                LanternControllerConstants.EMAIL_KEY);
        final int amount = Integer.parseInt(request.getParameter(
                    LanternControllerConstants.AMOUNT_KEY));
        log.info("Processing " + amount + "-cent donation " + id
                 + " from " + email);
        Dao dao = new Dao();
        int newBalance = dao.addCredit(email, amount);
        log.info(email + " has " + newBalance + " cents now.");
        if (newBalance >= LAUNCH_UPFRONT_COST) {
            if (dao.getUser(email) == null) {
                if (dao.getUser("lanterndonors@gmail.com") == null) {
                    dao.createUser("adamfisk@gmail.com",
                                   "lanterndonors@gmail.com");
                }
                dao.createUser("lanterndonors@gmail.com", email);
            }
            if (dao.getAndSetInstallerLocation(
                        email, LanternControllerConstants.FPS_PENDING_START)
                 == null) {
                String token = dao.getUser(email).getRefreshToken();
                if (token == null) {
                    // DRY warning: lantern_aws/salt/cloudmaster/cloudmaster.py
                    token = "<tokenless-donor>";
                }
                log.info("Launching server for " + email);
                InvitedServerLauncher.orderServerLaunch(email, token);
            }
        }
        LanternControllerUtils.populateOKResponse(response, "OK");
    }
}