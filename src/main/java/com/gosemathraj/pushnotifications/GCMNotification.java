package com.gosemathraj.pushnotifications;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/gcmnotification")
public class GCMNotification extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // The SENDER_ID here is the "Browser Key" that was generated when I
    // created the API keys for my Google APIs project.
    private static final String SENDER_ID = "AIzaSyDfLXwXeGDXgzZtWlSsCQ83Ea7EqMFpM6o";

    // This is a *cheat*  It is a hard-coded registration ID from an Android device
    // that registered itself with GCM using the same project id shown above.
    private static final String ANDROID_DEVICE = "f8VU8z63UVQ:APA91bFrEmGVWhsMP5g90D9mz3xXdxq0SdHhLQgcilpI3Ml3Lyq9P_9EQ-I6gGGBH2S7zTCdJUeW4tLZLxl8zA3OAH7CYitLZp0EbVE0blBWfhf-1_KIxf6qzPOH-TR9fru_b2UXbznG";

    // This array will hold all the registration ids used to broadcast a message.
    // for this demo, it will only have the ANDROID_DEVICE id that was captured
    // when we ran the Android client app through Eclipse.
    private List<String> androidTargets = new ArrayList<String>();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GCMNotification() {

        super();

        // we'll only add the hard-coded *cheat* target device registration id
        // for this demo.
        androidTargets.add(ANDROID_DEVICE);

    }

    // This doPost() method is called from the form in our index.jsp file.
    // It will broadcast the passed "Message" value.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // We'll collect the "CollapseKey" and "Message" values from our JSP page
        String collapseKey = "";
        String userMessage = "";

        try {
            userMessage = request.getParameter("Message");
            collapseKey = request.getParameter("CollapseKey");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Instance of com.android.gcm.server.Sender, that does the
        // transmission of a Message to the Google Cloud Messaging service.
        Sender sender = new Sender(SENDER_ID);

        // This Message object will hold the data that is being transmitted
        // to the Android client devices.  For this demo, it is a simple text
        // string, but could certainly be a JSON object.
        Message message = new Message.Builder()

                // If multiple messages are sent using the same .collapseKey()
                // the android target device, if it was offline during earlier message
                // transmissions, will only receive the latest message for that key when
                // it goes back on-line.
                .collapseKey(collapseKey)
                .timeToLive(30)
                .delayWhileIdle(true)
                .addData("message", userMessage)
                .build();

        try {
            // use this for multicast messages.  The second parameter
            // of sender.send() will need to be an array of register ids.
            MulticastResult result = sender.send(message, androidTargets, 1);

            if (result.getResults() != null) {
                int canonicalRegId = result.getCanonicalIds();
                if (canonicalRegId != 0) {

                }
            } else {
                int error = result.getFailure();
                System.out.println("Broadcast failure: " + error);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // We'll pass the CollapseKey and Message values back to index.jsp, only so
        // we can display it in our form again.
        request.setAttribute("CollapseKey", collapseKey);
        request.setAttribute("Message", userMessage);

        request.getRequestDispatcher("index.jsp").forward(request, response);

    }
}