package org.geworkbench.components.pathwaydecoder.remote;

import org.apache.axis.types.UnsignedInt;
import org.geworkbench.algorithms.BWAbstractAlgorithm;
import org.geworkbench.bison.datastructure.biocollections.microarrays.DSMicroarraySet;
import org.geworkbench.bison.datastructure.bioobjects.markers.DSGeneMarker;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.complex.panels.DSItemList;
import org.geworkbench.components.pathwaydecoder.PathwayMakerPanel;
import org.geworkbench.util.ProgressBar;
import javax.xml.rpc.holders.StringHolder;
import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import org.apache.axis.holders.UnsignedIntHolder;
import org.cu_genome.server.LoginToken;
import org.cu_genome.server.Parameter;
import org.cu_genome.server.ServerLocator;
import org.cu_genome.server.ServerPortType;

/**
 * <p>Title: SOAPCommunicator</p>
 * <p>Description: The class uses SOAP to run pathway decoder on a server.</p>
 * <p>Copyright: Copyright (c) 2003 -2004</p>
 * <p>Company: Columbia University</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SOAPCommunicator extends BWAbstractAlgorithm {
    private DSMicroarraySet<DSMicroarray> mset = null;

    private String host;
    private int port;
    private final String DEFAULT_HOST = "localhost";
    private final int DEFAULT_PORT = 9010;
    public int MARKER_CHUNK = 1000; //send CHUNK markers at a time
    private org.geworkbench.util.pathwaydecoder.mutualinformation.Parameter param = null;
    PathwayMakerPanel pmp = null;
    private ServerPortType service;
    private LoginToken token;


    public SOAPCommunicator(DSMicroarraySet mset,
                            org.geworkbench.util.pathwaydecoder.mutualinformation.Parameter param,
                            String host, String port) {
        this.mset = mset;
        this.param = param;
        setRemoteConn(host, port);
    }

    public void execute() {

        connect();
    }


    public void setRemoteConn(String host, String port){
        try{
            this.host = host;
            this.port = Integer.parseInt(port);
        }catch (Exception e){
            this.host = DEFAULT_HOST;
            this.port = DEFAULT_PORT;
        }
    }

    public double getCompletion() {
        double ret = 0;
        if (service != null && token != null){
            try {
                ret = service.getCompletion(token);
            }
            catch (RemoteException e) {
            }
        }
        return ret;
    }
    private void connect() {
        String urlString = new String("http://" + host + ":" + port);
        try {

            URL address = new URL(urlString);
            ServerLocator locator = new ServerLocator();
	    service = locator.getserver(address);
            UnsignedInt uid = null;

            try {
                uid = service.login("aner", "fust");
                token = new LoginToken();
                token.setUserId(uid);
            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
            UnsignedInt sid = service.createSession(uid, "session-1");
            token.setSessionId(sid);

            DSItemList<DSGeneMarker> markers = mset.getMarkers();
            loadMarkerarray(markers, token, service);

            int msetSize = mset.size();
            for (DSMicroarray ma : mset) {
                loadMicroarray(ma, token, service);
            }
            loadParameter(token, service);

            System.out.println("Method = " + param.method);
            ProgressBar pb = ProgressBar.create(ProgressBar.BOUNDED_TYPE);
            pb.setTitle("Create Network");
            pb.start();
            pb.setBounds(new ProgressBar.IncrementModel(0, 100, 0, 100, 1));
            pb.reset();
            pb.setMessage("Constructing Network ...");

            service.start(token, param.method);
            while (!service.isDone(token)) {
                try {
                    pb.updateTo((float)(100*getCompletion()));
                    Thread.sleep(100);
                } catch (Exception ex) {
                }
                ;
            }
            pb.stop();
            // set the result to the GeneNetworkPanel
            AdjacencyMatrix mat = new AdjacencyMatrix();
            byte[] gene = service.getGeneId(token);
            stop();
            if (gene != null) {
                int[] intgene = org.geworkbench.util.BinaryEncodeDecode.decodeUnsignedInt32(gene, 0, gene.length / org.geworkbench.util.BinaryEncodeDecode.SIZE_OF_INT);
                System.out.println("size: " + intgene.length);
                for (int i = 0; i < intgene.length; i++) {
                    System.out.print(" " + intgene[i] + ": ");
                    String tuple = service.getGeneMatrixRow(token, intgene[i], 0);
                    mat.read(intgene[i], tuple);
                }
            }
           File adjFile = new File(param.adjMatrixName);
            mat.print(mset, adjFile);
        } 
        catch (Exception exp) {
            System.out.println("Exception occured...." + exp.toString());
            exp.printStackTrace();
        }
    }

    private void loadParameter(LoginToken token, ServerPortType service) throws RemoteException {
        Parameter p = new Parameter();
        p.setCleanMatrix(false);        // param.eps=1 means reduce=false
        p.setMinMean(param.mean);
        p.setMinSigma(param.variance);
        p.setThreshold(param.miThreshold);
        //p.setEps(param.miErrorPercent);
        service.setParameter(token, p);
    }

    private void loadMicroarray(DSMicroarray micro, LoginToken token, ServerPortType service) throws RemoteException {
        int PRECISION = 10000;
        int microSize = micro.getMarkerNo();
        int markerValue[] = new int[microSize];
        for (int i = 0; i < microSize; i++) {
            markerValue[i] = (int) (micro.getMarkerValue(i).getValue() * PRECISION);
        }
        byte[] microArray = org.geworkbench.util.BinaryEncodeDecode.encodeUnsignedInt32(markerValue);
        service.addMicroArray(token, microArray, PRECISION);
    }

    /**
     * Loads Markers to the server. Markers are added in the order they
     * appear in the marker array.
     *
     * @param markers  DSMarker[] markers to load
     * @param token   LoginToken
     * @param service ServerPortType
     * @throws RemoteException
     */
    private void loadMarkerarray(DSItemList<DSGeneMarker> markers, LoginToken token, ServerPortType service) throws RemoteException {
        //send full chunks first
        int numOfChunk = (markers.size() / MARKER_CHUNK);
        int i = 0;
        for (i = 0; i < numOfChunk; ++i) {
            String markerAsString = markerToString(markers, i * MARKER_CHUNK, MARKER_CHUNK);
            service.setMarkerArray(token, markerAsString);
        }

        //send part chunk
        int partChunk = (markers.size() % MARKER_CHUNK);
        String markerAsString = markerToString(markers, i * MARKER_CHUNK, partChunk);
        service.setMarkerArray(token, markerAsString);
    }

    private String markerToString(DSItemList<DSGeneMarker> markers, int off, int length) {
        StringBuffer sb = new StringBuffer();
        int lastMarker = off + (length - 1);
        for (int i = off; i < (off + length); i++) {
            sb.append(markers.get(i).getSerial());
            sb.append('\t');
            sb.append(markers.get(i).getLabel());
            sb.append('\t');
            sb.append('T');
            sb.append('\t');
            sb.append('F');
            if (i < lastMarker) {
                sb.append('\t');
            }

        }
        return sb.toString();
    }

}
