package com.github.thegithubgeek.RemoteDesktop;

import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.google.gson.Gson;
import com.pubnub.api.*;
import com.pubnub.api.callbacks.*;
import com.pubnub.api.enums.*;
import com.pubnub.api.models.consumer.*;
import com.pubnub.api.models.consumer.pubsub.*;

public class Sender {
	static BufferedImage screen;
	static PubNub pubnub;
	static JFrame frame;
	static Panel panel;
	public static void main(String[] args) throws Exception{
		frame = new JFrame();
		panel = new Panel();
		panel.setSize(new Dimension(400, 400));
		panel.setBackground(Color.green);
		frame.setPreferredSize(new Dimension(500, 500));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.CENTER);
		frame.pack();
	    PNConfiguration pnConfiguration = new PNConfiguration();
	    pnConfiguration.setSubscribeKey("sub-c-b6dcb226-ff0b-11e6-a8c8-02ee2ddab7fe");
	    pnConfiguration.setPublishKey("pub-c-4f478a98-0264-40c2-a2af-5dec18497cbb");
	    
	    pubnub = new PubNub(pnConfiguration);
	 
	    pubnub.addListener(new SubscribeCallback() {
	        @Override
	        public void status(PubNub pubnub, PNStatus status) {
	            if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
	                // This event happens when radio / connectivity is lost
	            }
	 
	            else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
	 
	                // Connect event. You can do stuff like publish, and know you'll get it.
	                // Or just use the connected event to confirm you are subscribed for
	                // UI / internal notifications, etc
	             
	                if (status.getCategory() == PNStatusCategory.PNConnectedCategory){
	                    
	                }
	            }
	            else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
	 
	                // Happens as part of our regular operation. This event happens when
	                // radio / connectivity is lost, then regained.
	            }
	            else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
	 
	                // Handle messsage decryption error. Probably client configured to
	                // encrypt messages and on live data feed it received plain text.
	            }
	        }
	        String encoded = "";
	        @Override
	        public void message(PubNub pubnub, PNMessageResult message) {
	            // Handle new message stored in message.message
	            if (message.getChannel() != null) {
	            	if(message.getChannel().equals("RemoteDesktop"))
	            		System.out.println(message.getMessage().getAsString());
	            	else if(message.getChannel().equals("Screen")){
	            		System.out.println("Got Screen!");
	            		encoded += message.getMessage().getAsString();
	            		if(encoded.substring(encoded.length()-4).equals("xDxD")){
	            			ByteArrayInputStream bais = new ByteArrayInputStream(encoded.substring(0, encoded.length()-4).getBytes());
	            			try {
								screen = ImageIO.read(bais);
							} catch (IOException e) {e.printStackTrace();}
	            			System.out.println("faoij");
		            		panel.repaint();
	            		}
	            	}
	                // Message has been received on channel group stored in
	                // message.getChannel()
	            }
	            else {
	                // Message has been received on channel stored in
	                // message.getSubscription()
	            }
	 
	            /*
	                log the following items with your favorite logger
	                    - message.getMessage()
	                    - message.getSubscription()
	                    - message.getTimetoken()
	            */
	        }
	 
	        @Override
	        public void presence(PubNub pubnub, PNPresenceEventResult presence) {
	 
	        }
	    });
	    pubnub.subscribe().channels(Arrays.asList("RemoteDesktop")).execute();
	    pubnub.subscribe().channels(Arrays.asList("Screen")).execute();
	    Scanner in = new Scanner(System.in);
	    while(true){
	    	send(MouseInfo.getPointerInfo().getLocation().x + " " + MouseInfo.getPointerInfo().getLocation().y);
	    }
	}
	public static void send(String s){
		pubnub.publish().channel("RemoteDesktop").message(s).async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                // Check whether request successfully completed or not.
                if (!status.isError()) {
                	System.out.println("Sent!");
                    // Message successfully published to specified channel.
                }
                // Request processing failed.
                else {

                    // Handle message publish error. Check 'category' property to find out possible issue
                    // because of which request did fail.
                    //
                    // Request can be resent using: [status retry];
                }
            }
        });
	}
	static class Panel extends JPanel{
		protected void paintComponent(Graphics g){
			Robot r = null;
			try {
				r = new Robot();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//			screen = r.createScreenCapture(new Rectangle(screenSize));
//			screen = (BufferedImage) screen.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
			g.drawImage(screen, 0, 0, (int)(this.getHeight()*screenSize.getWidth()/screenSize.getHeight()), this.getHeight(), null);
		}
	}
}
