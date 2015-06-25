package com.mustillo.danielle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.InetAddress;

import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.CandidatePair;
import org.ice4j.ice.Component;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.IceProcessingState;
import org.ice4j.socket.IceSocketWrapper;

public class StateListener implements PropertyChangeListener {

	private InetAddress hostname;
	int port;

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof Agent) {
			Agent agent = (Agent) evt.getSource();
			if (agent.getState().equals(IceProcessingState.TERMINATED)) {
				// Your agent is connected. Terminated means ready to
				// communicate
				for (IceMediaStream stream : agent.getStreams()) {
					if (stream.getName().contains("video")) {
						Component rtpComponent = stream.getComponent(org.ice4j.ice.Component.RTP);
						CandidatePair rtpPair = rtpComponent.getSelectedPair();
						// We use IceSocketWrapper, but you can just use the UDP
						// socket
						// The advantage is that you can change the protocol
						// from UDP to TCP easily
						// Currently only UDP exists so you might not need to
						// use the wrapper.
						IceSocketWrapper wrapper = rtpPair.getIceSocketWrapper();
						// Get information about remote address for packet
						// settings
						TransportAddress ta = rtpPair.getRemoteCandidate().getTransportAddress();
						hostname = ta.getAddress();
						port = ta.getPort();
					}
				}
			}
		}
	}
}
