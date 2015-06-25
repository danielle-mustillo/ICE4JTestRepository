package com.mustillo.danielle;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.harvest.StunCandidateHarvester;
import org.ice4j.ice.harvest.TurnCandidateHarvester;

public class Ice4jTest {
	private static int CURRENT_COMPONENT_PORT = 5000;

	public static void generateIceMediaStream(Agent agent, Set<String> mediaNameSet, TransportAddress stunAddresses[],
			TransportAddress turnAddresses[]) throws IOException {

		IceMediaStream stream = null;

		/*
		 * We add STUN and TURN addresses as
		 * StunCandidateHarverster/TurnCandidateHarvester to the agent
		 */
		if (stunAddresses != null) {
			for (TransportAddress stunAddress : stunAddresses) {
				agent.addCandidateHarvester(new StunCandidateHarvester(stunAddress));
			}
		}

		if (turnAddresses != null) {
			for (TransportAddress turnAddress : turnAddresses) {
				agent.addCandidateHarvester(new TurnCandidateHarvester(turnAddress));
			}
		}
		/*
		 * For each mediaNameSet, create two components for it (signaling and
		 * data), each media will be separated by 50 port on average. Here ice
		 * candidates are gathered.
		 */
		for (String name : mediaNameSet) {
			stream = agent.createMediaStream(name);

			agent.createComponent(stream, Transport.UDP, CURRENT_COMPONENT_PORT, CURRENT_COMPONENT_PORT,
					CURRENT_COMPONENT_PORT + 50);

			agent.createComponent(stream, Transport.UDP, CURRENT_COMPONENT_PORT + 1, CURRENT_COMPONENT_PORT + 1,
					CURRENT_COMPONENT_PORT + 50);

			CURRENT_COMPONENT_PORT += 50;
		}

	}

	public static void main(String[] args) {

		Agent agent = new Agent();
		// This agent isn't controlling this ICE session (responder side)
		agent.setControlling(false);

		final Set mediaNameSet = new TreeSet();
		mediaNameSet.add("video");

		final TransportAddress[] stunAddresses = new TransportAddress[3];
		stunAddresses[0] = new TransportAddress("stun.services.mozilla.com", 3478, Transport.UDP);
		stunAddresses[1] = new TransportAddress("stun.ekiga.net", 3478, Transport.UDP);
		stunAddresses[2] = new TransportAddress("stun.ideasip.com", 3478, Transport.UDP);

		try {
			generateIceMediaStream(agent, mediaNameSet, stunAddresses, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}

		try {
			String toSend = SdpUtils.createSDPDescription(agent);
			System.out.println("Please copy the following to the other server");
			System.out.println(toSend);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} // Each computer sends this information
			// This information describes all the possible IP addresses and
			// ports

		System.out.println("Please type in the SDP from the other client:");
		Scanner scanner = new Scanner(System.in);
		StringBuilder string = new StringBuilder();
		String last = "";
		do {
			last = scanner.nextLine();
			if (last.contains("doneDONE"))
				break;
			else
				string.append(last + "\n");
		} while (true);
		String remoteReceived = string.toString(); // This information
													// was grabbed
													// from the
		// server, and shouldn't be empty.
		try {
			SdpUtils.parseSDP(agent, remoteReceived);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} // This will add the remote
			// information to the agent.

		agent.addStateChangeListener(new StateListener()); // We will
															// define this
															// class soon
		// You need to listen for state change so that once connected you can
		// then use the socket.
		agent.startConnectivityEstablishment(); // This will do all the work for
												// you to connect
	}

}
