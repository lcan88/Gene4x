package org.geworkbench.bison.datastructure.biocollections.microarrays;

import org.geworkbench.bison.annotation.CSAnnotationContext;
import org.geworkbench.bison.annotation.CSAnnotationContextManager;
import org.geworkbench.bison.annotation.DSAnnotationContext;
import org.geworkbench.bison.datastructure.bioobjects.DSBioObject;
import org.geworkbench.bison.datastructure.bioobjects.markers.CSExpressionMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.genotype.CSGenotypeMarker;
import org.geworkbench.bison.datastructure.bioobjects.markers.genotype.GenotypeColorContext;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSGenotypicMarkerValue;
import org.geworkbench.bison.datastructure.bioobjects.microarray.CSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMicroarray;
import org.geworkbench.bison.datastructure.bioobjects.microarray.DSMutableMarkerValue;
import org.geworkbench.bison.parsers.resources.Resource;
import org.geworkbench.bison.util.RandomNumberGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * <p>Title: Sequence and Pattern Plugin</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class CSGenotypeMicroarraySet extends CSMicroarraySet<DSMicroarray> implements Serializable {
    //static private ImageIcon icon      = new ImageIcon(MicroarrayDataSet.class.getResource("ma.gif"));
    private HashMap properties = new HashMap();
    private ArrayList descriptions = new ArrayList();
    private String label = "Undefined";
    private Resource maResource = null;
    private org.geworkbench.bison.parsers.DataParseContext dataContext = new org.geworkbench.bison.parsers.DataParseContext();
    private boolean initialized = false;

    public CSGenotypeMicroarraySet() {
        super(RandomNumberGenerator.getID(), "");
        addObject(org.geworkbench.bison.util.colorcontext.ColorContext.class, new GenotypeColorContext());
        /** @todo Remove if not used */
        //        addObject(DSRangeMarker.class, CSGenotypeMarker.class);
        addDescription("Genotype data");
        type = snpType;
    }

    public File getFile() {
        return file;
    }

    public void put(Object key, Object value) {
        properties.put(key, value);
    }

    public Object get(Object key) {
        return properties.get(key);
    }

    public void setResource(Resource resource) {
        maResource = (Resource) resource;
    }

    public void removeResource(Resource resource) {
        maResource = null;
    }

    public void readFromResource() {

    }

    public void writeToResource() {

    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean flag) {
        dirty = flag;
    }

    public String[] getDescriptions() {
        String[] descr = new String[descriptions.size()];
        for (int i = 0; i < descriptions.size(); i++) {
            descr[i] = (String) descriptions.get(i);
        }
        return descr;
    }

    public void removeDescription(String descr) {
        descriptions.remove(descr);
    }

    public void addDescription(String descr) {
        descriptions.add(descr);
    }

    public String getDataSetName() {
        return label;
    }

    public void read(File _file) {
        file = _file;
        label = file.getName();
        readFromFile(file);
    }

    public String getName() {
        return label;
    }

    // Reads the actual input file (Affy, GenePix, etc) that contains the
    // data.
    public void readFromFile(File file) {
        GenotypeParser parser = new GenotypeParser();
        BufferedReader reader;
        this.label = file.getName();
        this.absPath = file.getAbsolutePath();
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException fnf) {
            return;
        }
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                parser.parseLine(line, this);
            }
            reader.close();
        } catch (Exception ioe) {
            int i = 0;
            return;
        } finally {
        }
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException fnf) {
            return;
        }
        initialize(parser.microarrayNo, parser.markerNo);
        try {
            while ((line = reader.readLine()) != null) {
                parser.executeLine(line, this);
            }
        } catch (Exception ioe) {
            System.out.println("Error while parsing line: " + line);
            return;
        } finally {
            //            setPhenotype();
        }
    }

    public class GenotypeParser {
        private boolean parseByMicroarray = false;
        public int microarrayNo = 0;
        public int markerNo = 0;
        public int propNo = 0;
        int currGeneId = 0;
        int currMicroarrayId = 0;
        Vector phenotypes = new Vector();

        void executeLine(String line, DSMicroarraySet<? extends DSBioObject> mArraySet) {
            CSAnnotationContextManager manager = CSAnnotationContextManager.getInstance();
            StringTokenizer st = new StringTokenizer(line, "\t\n\r");
            if (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.equalsIgnoreCase("NullSigma")) {
                } else if (token.equalsIgnoreCase("GTData")) {
                } else if (token.equalsIgnoreCase("MaxSigma")) {
                } else if (token.equalsIgnoreCase("ColumnsAreMarkers")) {
                } else if (token.equalsIgnoreCase("DeltaSigma")) {
                } else if (token.equalsIgnoreCase("LogValues")) {
                } else if (token.equalsIgnoreCase("AddValue")) {
                } else if (token.equalsIgnoreCase("MinValue")) {
                } else if (token.equalsIgnoreCase("MinSupport")) {
                } else if (token.equalsIgnoreCase("MinMarkers")) {
                } else if (token.equalsIgnoreCase("Phenotype")) {
                } else if (token.equalsIgnoreCase("PDFModel")) {
                } else if (token.equalsIgnoreCase("SortElements")) {
                } else if (token.equalsIgnoreCase("SNPData")) {
                } else if (token.equalsIgnoreCase("AlleleData")) {
                } else if (parseByMicroarray && token.equalsIgnoreCase("Accession")) {
                    int i = 0;
                    int j = 0;
                    while (st.hasMoreTokens()) {
                        token = st.nextToken();
                        if ((token.length() > 1) && token.substring(0, 2).equalsIgnoreCase("Ph")) {
                            phenotypes.add(i, token);
                            i++;
                        } else {
                            if (markerVector.get(j) == null) {
                                markerVector.add(j, new CSGenotypeMarker(j));
                            }
                            markerVector.get(j).setLabel(token);
                            markerVector.get(j).setDescription(token);
                            j++;
                        }
                    }
                } else if (!parseByMicroarray && token.equalsIgnoreCase("Description")) {
                    // you can read cell variable here
                    String phLabel = st.nextToken();
                    boolean isAccession = phLabel.equalsIgnoreCase("Accession");
                    DSAnnotationContext context = manager.getContext(mArraySet, phLabel);
                    CSAnnotationContext.initializePhenotypeContext(context);
                    int i = 0;
                    while (st.hasMoreTokens()) {
                        String nextToken = st.nextToken();
                        if (isAccession) {
                            get(i++).setLabel(nextToken);
                        } else {
                            context.labelItem(mArraySet.get(i++), nextToken);
                        }
                    }
                } else if (token.equalsIgnoreCase("ID")) {
                    int geneId = 0;
                    for (int propId = 0; propId < phenotypes.size(); propId++) {
                        String value = st.nextToken();
                        phenotypes.set(propId, value);
                    }
                    while (st.hasMoreTokens()) {
                        String value = st.nextToken();
                        if (markerVector.get(geneId) == null) {
                            switch (type) {
                                case DSMicroarraySet.snpType:
                                    markerVector.add(geneId, new CSGenotypeMarker(geneId));
                                    break;
                                case DSMicroarraySet.alleleType:
                                    markerVector.add(geneId, new CSGenotypeMarker(geneId));
                                    break;
                                case DSMicroarraySet.geneExpType:
                                    markerVector.add(geneId, new CSExpressionMarker(geneId));
                                    break;
                            }
                        }
                        markerVector.get(geneId).setLabel(token);
                        geneId++;
                    }
                } else if (line.charAt(0) != '\t') {
                    if (parseByMicroarray) {
                        int geneId = 0;
                        get(currMicroarrayId).setLabel(token);
                        for (int propId = 0; propId < phenotypes.size(); propId++) {
                            String propLabel = (String) phenotypes.get(propId);
                            String nextToken = st.nextToken();
                            DSAnnotationContext context = manager.getContext(mArraySet, propLabel);
                            CSAnnotationContext.initializePhenotypeContext(context);
                            context.labelItem(mArraySet.get(currMicroarrayId), nextToken);
                        }
                        while (st.hasMoreTokens()) {
                            String value = st.nextToken();
                            DSMutableMarkerValue marker = (DSMutableMarkerValue) get(currMicroarrayId).getMarkerValue(geneId);
                            parse(marker, value);
                            //markerVector.get(geneId).check(marker, false);
                            geneId++;
                        }
                        currMicroarrayId++;
                    } else {
                        int i = 0;
                        if (markerVector.get(currGeneId) == null) {
                            CSGenotypeMarker mi = new CSGenotypeMarker(currGeneId);
                            mi.reset(currGeneId, microarrayNo, microarrayNo);
                            markerVector.add(currGeneId, mi);
                        }
                        markerVector.get(currGeneId).setDescription(token);
                        boolean pValueExists = ((st.countTokens() - 2) > microarrayNo);
                        token = st.nextToken();
                        markerVector.get(currGeneId).setLabel(token);
                        while (st.hasMoreTokens()) {
                            DSMutableMarkerValue marker = (DSMutableMarkerValue) get(i).getMarkerValue(currGeneId);
                            String value = st.nextToken();
                            String status = null;
                            if (pValueExists)
                                status = st.nextToken();
                            else
                                status = "P";
                            if ((type == snpType) || (type == alleleType)) {
                                parse(marker, value, status);
                                //markerVector.get(currGeneId).check(marker, false);
                            } else {
                                parse(marker, value, status);
                                //markerVector.get(currGeneId).check(marker, false);
                            }
                            //CheckGene(marker.GetValue());
                            if (marker.isMasked() || marker.isMissing()) {
                                maskedSpots++;
                            }
                            i++;
                        }
                        currGeneId++;
                    }
                }
            }
        }

        void parseLine(String line, DSMicroarraySet mArraySet) {
            StringTokenizer st = new StringTokenizer(line, "\t\n\r");
            if (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (token.equalsIgnoreCase("GTData")) {
                    type = snpType;
                    dataContext.gtBase = Integer.parseInt(st.nextToken());
                } else if (token.equalsIgnoreCase("ColumnsAreMarkers")) {
                    parseByMicroarray = true;
                } else if (token.equalsIgnoreCase("NullSigma")) {
                    dataContext.sigma0 = Double.parseDouble(st.nextToken());
                    mArraySet.addObject("Sigma0", new Double(dataContext.sigma0));
                } else if (token.equalsIgnoreCase("MaxSigma")) {
                    dataContext.maxSigma = Double.parseDouble(st.nextToken());
                    mArraySet.addObject("Sigma1", new Double(dataContext.maxSigma));
                } else if (token.equalsIgnoreCase("DeltaSigma")) {
                    dataContext.deltaSigma = Double.parseDouble(st.nextToken());
                    mArraySet.addObject("DeltaSigma", new Double(dataContext.deltaSigma));
                } else if (token.equalsIgnoreCase("LogValues")) {
                    dataContext.isLog = true;
                    if (dataContext.minValue <= 0) {
                        dataContext.minValue = 1;
                    }
                } else if (token.equalsIgnoreCase("AddValue")) {
                    dataContext.addValue = Double.parseDouble(st.nextToken());
                    mArraySet.addObject("AddValue", new Double(dataContext.addValue));
                } else if (token.equalsIgnoreCase("MinValue")) {
                    dataContext.minValue = Double.parseDouble(st.nextToken());
                    mArraySet.addObject("MinValue", new Double(dataContext.minValue));
                } else if (token.equalsIgnoreCase("PDFModel")) {
                    dataContext.pdfMode = st.nextToken();
                    mArraySet.addObject("PDFMode", dataContext.pdfMode);
                } else if (token.equalsIgnoreCase("MinSupport")) {
                    dataContext.minSupport = Integer.parseInt(st.nextToken());
                    mArraySet.addObject("MinSupport", new Double(dataContext.minSupport));
                } else if (token.equalsIgnoreCase("MinMarkers")) {
                    dataContext.minMarkers = Integer.parseInt(st.nextToken());
                    mArraySet.addObject("MinMarkerNo", new Double(dataContext.minMarkers));
                } else if (token.equalsIgnoreCase("Phenotype")) {
                    /** @todo Add correct selection of phenotype tags based on criteria
                     PhenoProperty selection = new PhenoProperty(st.nextToken());
                     Phenotype.SetSelectedPhenoProperty(st.nextToken(), st.nextToken());
                     */
                    st.nextToken();
                } else if (token.equalsIgnoreCase("SortElements")) {
                    dataContext.sortKey = st.nextToken();
                    mArraySet.addObject("SortKey", dataContext.sortKey);
                } else if (token.equalsIgnoreCase("ID")) {
                } else if (token.equalsIgnoreCase("SNPData")) {
                    type = snpType;
                } else if (token.equalsIgnoreCase("AlleleData")) {
                    type = alleleType;
                    dataContext.isGenotype = false;
                } else if (parseByMicroarray && token.equalsIgnoreCase("Accession")) {
                    while (st.hasMoreTokens()) {
                        token = st.nextToken();
                        if ((token.length() > 1) && token.substring(0, 2).equalsIgnoreCase("Ph")) {
                            propNo++;
                        } else {
                            markerNo++;
                        }
                    }
                } else if (!parseByMicroarray && token.equalsIgnoreCase("Description")) {
                    // you can read cell variable here
                    String value = st.nextToken();
                    if ((size() == 0) && value.equalsIgnoreCase("Accession")) {
                        while (st.hasMoreTokens()) {
                            st.nextToken();
                            microarrayNo++;
                        }
                    } else {
                        propNo++;
                    }
                } else if (line.charAt(0) != '\t') {
                    if (parseByMicroarray) {
                        microarrayNo++;
                    } else {
                        markerNo++;
                    }
                }
            }
        }
    }

    public void parse(DSMutableMarkerValue marker, String value) {
        if (marker instanceof CSGenotypicMarkerValue) {
            CSGenotypicMarkerValue gtMarker = (CSGenotypicMarkerValue) marker;
            int a1;
            int a2;
            String[] parseableValue = value.split(":");
            String[] allele = parseableValue[parseableValue.length - 1].split("[| /]");
            switch (allele.length) {
                case 0:
                    gtMarker.setMissing(true);
                    gtMarker.setGenotype(0, 0);
                    break;
                case 1:
                    int v = Integer.parseInt(allele[0]);
                    if (gtMarker.isGT) {
                        gtMarker.setGenotype(v / dataContext.gtBase, v % dataContext.gtBase);
                    } else {
                        gtMarker.setAllele(v);
                    }
                    if (v == 0) {
                        gtMarker.setMissing(true);
                    } else {
                        gtMarker.setPresent();
                    }
                    break;
                case 2:
                    a1 = Integer.parseInt(allele[0]);
                    a2 = Integer.parseInt(allele[1]);
                    gtMarker.setGenotype(a1, a2);
                    if ((a1 == 0) || (a2 == 0)) {
                        //SetAbsent();
                        gtMarker.setMissing(true);
                    } else {
                        gtMarker.setPresent();
                    }
                    break;
                default:
                    a1 = Integer.parseInt(allele[0]);
                    a2 = Integer.parseInt(allele[allele.length - 1]);
                    gtMarker.setGenotype(a1, a2);
                    if ((a1 == 0) || (a2 == 0)) {
                        //SetAbsent();
                        gtMarker.setMissing(true);
                    } else {
                        gtMarker.setPresent();
                    }
                    break;

            }
        }
    }

    public void parse(DSMutableMarkerValue marker, String value, String status) {
        if (marker instanceof CSGenotypicMarkerValue) {
            CSGenotypicMarkerValue gtMarker = (CSGenotypicMarkerValue) marker;
            try {
                char c = status.charAt(0);
                if (Character.isLowerCase(c)) {
                    gtMarker.mask();
                }
                switch (Character.toUpperCase(c)) {
                    case 'P':
                        gtMarker.setPresent();
                        break;
                    case 'A':
                        gtMarker.setAbsent();
                        break;
                    case 'M':
                        gtMarker.setMarginal();
                        break;
                    default:
                        gtMarker.setMissing(true);
                        break;
                }
                parse(marker, value);
            } catch (NumberFormatException e) {
                gtMarker.setGenotype(0, 0);
                gtMarker.setMissing(true);
            }
        }
    }

    public void writeToFile(String fileName) {
        File file = new File(fileName);
        //save(file);
    }

    public int getPlatformType() {
        return GENOTYPE_PLATFORM;
    }

    public DSMicroarraySet toHaplotype() {
        CSGenotypeMicroarraySet newChips = new CSGenotypeMicroarraySet();
        newChips.label = label + " (Haplotype)";
        newChips.absPath = absPath;
        newChips.maskedSpots = maskedSpots * 2;
        for (int geneId = 0; geneId < 2 * getMarkers().size(); geneId++) {
            CSGenotypeMarker m = new CSGenotypeMarker(geneId);
            m.reset(geneId, size(), size());
            newChips.markerVector.add(m);
        }
        for (int chipId = 0; chipId < size(); chipId++) {
            DSMicroarray oldChip = get(chipId);
            DSMicroarray newChip = new CSMicroarray(oldChip.getSerial(), oldChip.getMarkerNo() * 2, oldChip.getLabel(), null, null, false, DSMicroarraySet.snpType);
            //newChip.setPropertyValueMap(oldChip.getPropertyValueMap());
            for (int geneId = 0; geneId < markerVector.size(); geneId++) {
                CSGenotypicMarkerValue gt = (CSGenotypicMarkerValue) oldChip.getMarkerValue(geneId);
                CSGenotypicMarkerValue m1 = (CSGenotypicMarkerValue) newChip.getMarkerValue(2 * geneId);
                CSGenotypicMarkerValue m2 = (CSGenotypicMarkerValue) newChip.getMarkerValue(2 * geneId + 1);
                int v1 = gt.getAllele(0);
                int v2 = gt.getAllele(1);
                if (v1 < v2) {
                    m1.setAllele(v1);
                    m2.setAllele(v2);
                } else {
                    m1.setAllele(v2);
                    m2.setAllele(v1);
                }
                ((CSGenotypeMarker) newChips.get(2 * geneId)).check(m1, false);
                ((CSGenotypeMarker) newChips.get(2 * geneId + 1)).check(m2, false);
                newChip.getMarkerValues()[2 * geneId].setConfidence(oldChip.getMarkerValue(geneId).getConfidence());
                newChip.getMarkerValues()[2 * geneId + 1].setConfidence(oldChip.getMarkerValue(geneId).getConfidence());
            }
            newChips.add(newChip);
        }
        for (int geneId = 0; geneId < markerVector.size(); geneId++) {
            newChips.getMarkers().get(2 * geneId).setLabel(getMarkers().get(geneId).getLabel() + "_1");
            newChips.getMarkers().get(2 * geneId + 1).setLabel(getMarkers().get(geneId).getLabel() + "_2");
            newChips.getMarkers().get(2 * geneId).setDescription(getMarkers().get(geneId).getDescription() + "_1");
            newChips.getMarkers().get(2 * geneId + 1).setDescription(getMarkers().get(geneId).getLabel() + "_2");
        }
        return newChips;
    }

    public void initialize(int maNo, int mrkNo) {
        // this is required so that the microarray vector may create arrays of the right size
        for (int microarrayId = 0; microarrayId < maNo; microarrayId++) {
            add(microarrayId, new CSMicroarray(microarrayId, mrkNo, "Test", null, null, false, type));
        }
        for (int i = 0; i < mrkNo; i++) {
            CSGenotypeMarker mi = new CSGenotypeMarker(i);
            mi.reset(i, maNo, mrkNo);
            markerVector.add(i, mi);
        }
        initialized = true;
    }
}
