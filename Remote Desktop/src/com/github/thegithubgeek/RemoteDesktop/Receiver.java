package com.github.thegithubgeek.RemoteDesktop;

import java.awt.Robot;
import java.util.Arrays;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

public class Receiver {
	public static void main(String[] args) throws Exception{
		Robot robot = new Robot();
	    PNConfiguration pnConfiguration = new PNConfiguration();
	    pnConfiguration.setSubscribeKey("sub-c-b6dcb226-ff0b-11e6-a8c8-02ee2ddab7fe");
	    pnConfiguration.setPublishKey("pub-c-4f478a98-0264-40c2-a2af-5dec18497cbb");
	    
	    PubNub pubnub = new PubNub(pnConfiguration);
	 
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
	 
	        @Override
	        public void message(PubNub pubnub, PNMessageResult message) {
	            // Handle new message stored in message.message
	            if (message.getChannel() != null) {
	            	String msg = message.getMessage().getAsString();
	            	System.out.println(msg);
	            	String[] xy = msg.split(" ");
	            	int x = Integer.valueOf(xy[0]);
	            	int y = Integer.valueOf(xy[1]);
	            	robot.mouseMove(x,y);
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
	}
}
