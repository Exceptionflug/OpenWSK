package net.thecobix.stats.client;

import java.io.IOException;

public class KeepAliveRunnable implements Runnable {

	private Client client;
	
	public KeepAliveRunnable(Client client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		while(this.client.isConnected()) {
			long curr = System.currentTimeMillis();
			long dur = curr - this.client.now;
			if(dur > 10000 && this.client.sended == false) {
				this.client.sended = true;
				this.client.pStart = System.currentTimeMillis();
				try {
					this.client.send("COMMAND: keepalive");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(dur > 30000 && this.client.sended) {
				this.client.disconnect();
				return;
			}
		}
	}

}
