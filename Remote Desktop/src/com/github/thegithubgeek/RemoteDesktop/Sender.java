package com.github.thegithubgeek.RemoteDesktop;

import java.awt.Robot;
import java.util.*;

import com.pubnub.api.*;
import com.pubnub.api.callbacks.*;
import com.pubnub.api.enums.*;
import com.pubnub.api.models.consumer.*;
import com.pubnub.api.models.consumer.pubsub.*;

public class Sender {
	static PubNub pubnub;
	public static void main(String[] args) throws Exception{
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
	 
	        @Override
	        public void message(PubNub pubnub, PNMessageResult message) {
	            // Handle new message stored in message.message
	            if (message.getChannel() != null) {
	            	System.out.println(message.getMessage().getAsString());
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
	    Scanner in = new Scanner(System.in);
	    while(true){
	    	String msg;
	    	if((msg=in.nextLine())!=null){
	    		send(msg);
	    	}
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
}
