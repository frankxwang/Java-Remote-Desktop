package com.github.thegithubgeek.RemoteDesktop;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Timer;
import java.util.zip.GZIPOutputStream;

import javax.imageio.*;
import javax.imageio.stream.*;
import javax.swing.*;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

public class Receiver {
	static PubNub pubnub;
	static Robot robot;
	public static void main(String[] args) throws Exception{
		robot = new Robot();
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
	            	if(message.getChannel().equals("RemoteDesktop")){
		            	String msg = message.getMessage().getAsString();
		            	System.out.println(msg);
		            	String[] xy = msg.split(" ");
		            	int x = Integer.valueOf(xy[0]);
		            	int y = Integer.valueOf(xy[1]);
		            	robot.mouseMove(x,y);
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
	    pubnub.subscribe().channels(Arrays.asList("RemoteDesktop", "Screen")).execute();
	    Timer t = new Timer();
//	    t.schedule(new sendScreen(), 0, 100000000);
	}
	static class sendScreen extends TimerTask{
		@Override
		public void run(){
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			BufferedImage screen = robot.createScreenCapture(new Rectangle(screenSize));
			ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
			ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
			jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jpgWriteParam.setCompressionQuality(0.7f);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			MemoryCacheImageOutputStream stream = new MemoryCacheImageOutputStream(baos);
			jpgWriter.setOutput(stream);
			IIOImage outputImage = new IIOImage(screen, null, null);
			try {
				jpgWriter.write(null, outputImage, jpgWriteParam);
				baos.flush();
			} catch (IOException e1) {}
			jpgWriter.dispose();
			byte[] bytearr = baos.toByteArray();
			try {
				baos.close();
			} catch (Exception e) {}
			String encoded = new String(bytearr);
			System.out.println(encoded.length());
			int index = 0;
			int size = 2500;
			for (int i = 0; i < encoded.length()/size; i++) {
				index = (i+1)*size;
				pubnub.publish().channel("Screen").message(encoded.substring(i*size, (i+1)*size-1)).async(new PNCallback<PNPublishResult>() {
		            @Override
		            public void onResponse(PNPublishResult result, PNStatus status) {
		                if (!status.isError()) {
		                } else {
		                	System.out.println("Fail");
		                	System.out.println(status.getErrorData());
		                }
		            }
				});
			}
			pubnub.publish().channel("Screen").message(encoded.substring(index, encoded.length()-1)+"xDxD").async(new PNCallback<PNPublishResult>() {
	            @Override
	            public void onResponse(PNPublishResult result, PNStatus status) {
	                if (!status.isError()) {
	                	System.out.println("Sent!");
	                } else {
	                	System.out.println("Fail");
	                	System.out.println(status.getErrorData());
	                }
	            }
			});
		}
	}
}
