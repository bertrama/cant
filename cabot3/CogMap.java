// Kailash

import java.util.*;

class CogMap {
	// nets
	private CABot3Net RoomNet;
	private CABot3Net RoomNet2;
	private CABot3Net CogSeqNet;

	private CABot3Net FactNet;

	// -------

	private static final int NEURON_BLOCK_SIZE = 200;	// the size of door, mem patterns etc.
	private static final String nullXml = "./cabot3/data/null.xml";
	private int lastStep = -1;
	private static final int waitSteps = 100;	// number of cycles for which a pattern is to be learned

	private static final double externalActivation = 10.0;


	// --- initialize
  public void init() {
    // cogmap specific nets
	RoomNet = (CABot3Net)CANTExperiment.getNet("RoomNet");
	RoomNet2 = (CABot3Net)CANTExperiment.getNet("Room2Net");
	CogSeqNet = (CABot3Net)CANTExperiment.getNet("CogSeqNet");
	FactNet = (CABot3Net)CANTExperiment.getNet("FactNet");
   }


	// --- sets up connections between nets
	public void setConnections() {

		System.out.println("Cog - Connecting cog nets");

		// ======================== roomnet inhibits 'seenroom' in factnet
		/*
		for (int i = 0; i < RoomNet.getSize(); i++) {
			if (RoomNet.neurons[i].isInhibitory()) {
				int toNeuron = (i%40)+760;
				RoomNet.neurons[i].addConnection(FactNet.neurons[toNeuron],-3.0);

				toNeuron = ((i+1)%40)+760;
				RoomNet.neurons[i].addConnection(FactNet.neurons[toNeuron],-3.0);
				toNeuron = ((i+2)%40)+760;
				RoomNet.neurons[i].addConnection(FactNet.neurons[toNeuron],-3.0);
				toNeuron = (i%40)+800;
				RoomNet.neurons[i].addConnection(FactNet.neurons[toNeuron],-2.0);

				toNeuron = ((i+1)%40)+800;
				RoomNet.neurons[i].addConnection(FactNet.neurons[toNeuron],-2.0);
				toNeuron = ((i+2)%40)+800;
				RoomNet.neurons[i].addConnection(FactNet.neurons[toNeuron],-3.0);
			}
		}
		*/

		double weight = .001;

		/*
		// ======================== cur room -> last room
		for(int room=0; room<4; room++) {
			int from = room*200;
			int to = from+200;

			for(int i=from; i < to; i++) {
				if(!RoomNet.neurons[i].isInhibitory()) {
					for(int syn=0; syn<3; syn++) {
						RoomNet.neurons[i].addConnection(RoomNet2.neurons[getRandRange(from, to)], 1.5);
					}
				}
			}
		}
		*/

		// ======================== rooms->seqnet

		for(int i=0; i<RoomNet.getSize(); i++) {
			if(!RoomNet.neurons[i].isInhibitory()) {
				for(int syn=0; syn<10; syn++) {
					RoomNet.neurons[i].addConnection(CogSeqNet.neurons[getRandRange(0, CogSeqNet.getSize())], weight);
				}
			}
		}

		for(int i=0; i<CogSeqNet.getSize(); i++) {
			if(!CogSeqNet.neurons[i].isInhibitory()) {
				for(int syn=0; syn<25; syn++) {
					CogSeqNet.neurons[i].addConnection(RoomNet.neurons[getRandRange(0, RoomNet.getSize())], weight);
					CogSeqNet.neurons[i].addConnection(RoomNet2.neurons[getRandRange(0, RoomNet2.getSize())], weight);
				}
			}
		}

		// ======================== rooms2->sequence->room2
		for(int i=0; i<RoomNet2.getSize(); i++) {
			if(!RoomNet2.neurons[i].isInhibitory()) {
				RoomNet2.neurons[i].addConnection(CogSeqNet.neurons[getRandRange(0, CogSeqNet.getSize())], weight);
			}
		}
	}

	// this runs every step
	public void cogRun(int step) {

	}


	/******************************* Generic *****************************/

	// turn learning off on all cog nets
	public void turnLearningOff() {
		RoomNet.setLearningOn(false);
		RoomNet2.setLearningOn(false);
		CogSeqNet.setLearningOn(false);
		FactNet.setLearningOn(false);
	}

	// turn learning on, on given nets
	public void turnLearningOn(String nets[]) {
		turnLearningOff();

		CABot3Net tmp;
		for(int i=0; i<nets.length; i++) {
			tmp = (CABot3Net)CANTExperiment.getNet(nets[i]);
			tmp.setLearningOn(true);
		}

	}


	public void makeWindows() {

		System.out.println("- Cog, make windows");

		// add captions to nets
		String captions[] = new String[] {"VP", "HP", "VS", "HS"};
		int vpos=50;
		for(int i=0; i<captions.length; i++) {
			RoomNet.cantFrame.matrix.addStringsToPrint(captions[i],vpos, 140);
			RoomNet2.cantFrame.matrix.addStringsToPrint(captions[i],vpos, 140);
			vpos+=60;
		}

		captions = new String[] {"R1-R2", "R2-R3", "R3-R4", "R4-R1"};
		vpos=50;
		for(int i=0; i<captions.length; i++) {
			CogSeqNet.cantFrame.matrix.addStringsToPrint(captions[i],vpos, 140);
			vpos+=60;
		}

		// size and position windows
		RoomNet.cantFrame.setSize(250,425);
		RoomNet.cantFrame.setLocation(0, 400);

		CogSeqNet.cantFrame.setSize(300,425);
		CogSeqNet.cantFrame.setLocation(250,400);

		RoomNet2.cantFrame.setSize(250,425);
		RoomNet2.cantFrame.setLocation(550,400);

				// hide all cabot3 windows except
		/*		Enumeration eNum = CANT23.nets.elements();
		while (eNum.hasMoreElements()) {
			CABot3Net net = (CABot3Net)eNum.nextElement();
			net.cantFrame.hide();
			}*/
	}



	/******************************* PRIVATE METHODS *****************************/

	// something triggered learning, stop learning after 'waitSteps' steps
	private void learningStarted() {
		lastStep = CANT23.CANTStep;
	}

	// clear activation on cog nets :)
	private void clearNets() {
		CogSeqNet.clear();
		FactNet.clear();
		RoomNet.clear();
		RoomNet2.clear();
		//CogGoalNet.clear();
	}

	// creates a block pattern for rooms, mem net etc.
	private int[] getBlockPattern(int room) {
		room = room-1;

		int pattern[] = new int[NEURON_BLOCK_SIZE-50];
		int j=0;

		int from=(room*NEURON_BLOCK_SIZE);
		int to = from+NEURON_BLOCK_SIZE;

/*
		for(int i=from; i<to; i++) {
			pattern[j] = i;
			j++;
		}
*/

		for(int i=0; i<NEURON_BLOCK_SIZE-50; i++) {
			pattern[j] = getRandRange(from, to);
			j++;
		}

		return pattern;
	}

	// get random numbers in a range, specific for array mapping
	// eg: a=1, b=100
	// output:  between 1 (included) to 99
	private int getRandRange(int a, int b) {
		b=b-1;

		int range = b - a + 1;

		Random rnd = new Random();

    	long fraction = (long)(range * rnd.nextDouble());
		int randomNumber =  (int)(fraction + a);

		return randomNumber;
	}

}
