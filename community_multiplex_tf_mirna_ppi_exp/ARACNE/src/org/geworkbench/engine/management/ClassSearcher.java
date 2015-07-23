package org.geworkbench.engine.management;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Indexes a classpath so that classes can be searched hierarchically according to class structure.
 *
 * @author John Watkinson
 */
public class ClassSearcher {

    static Log log = LogFactory.getLog(ClassSearcher.class);

    private static class ClassNode {
        private String className;
        private Map<String,ClassNode> children;
        private boolean local;
        private boolean abstractClass;

        public ClassNode(String className) {
            this.className = className;
            children = null;
        }

        public String getClassName() {
            return className;
        }

        public void addChild(ClassNode child) {
            if (children == null) {
                children = new HashMap<String,ClassNode>();
            }
            children.put(child.getClassName(), child);
        }

        public ClassNode getChild(String childName) {
            if (children == null) {
                return null;
            }
            return (ClassNode) children.get(childName);
        }

        public Collection<ClassNode> getAllChildren() {
            if (children == null) {
                return null;
            }
            return children.values();
        }

        public boolean isLocal() {
            return local;
        }

        public void setLocal(boolean local) {
            this.local = local;
        }

        public boolean isAbstractClass() {
            return abstractClass;
        }

        public void setAbstractClass(boolean abstractClass) {
            this.abstractClass = abstractClass;
        }
    }

    private Set<ClassNode> topSet = new HashSet<ClassNode>();
    private Map<String,ClassNode> nameMap = new HashMap<String,ClassNode>();
    private URL[] urls;

    public ClassSearcher(URL[] urls) {
        this.urls = urls;
        initClassTree();
    }

    private ClassNode getClassNode(String className) {
        ClassNode node = (ClassNode) nameMap.get(className);
        if (node == null) {
            node = new ClassNode(className);
            nameMap.put(className, node);
            topSet.add(node);
        }
        return node;
    }

    private void relate(ClassNode parent, ClassNode child) {
        parent.addChild(child);
        topSet.remove(child);
    }

    private void indexClass(InputStream classStream, String filename) throws IOException {
        // log.debug("Indexing '" + filename + "'...");
        ClassParser parser = new ClassParser(classStream, filename);
        JavaClass jc = parser.parse();
        String className = jc.getClassName();
        ClassNode node = getClassNode(className);
        node.setLocal(true);
        if (jc.isInterface() || jc.isAbstract()) {
            node.setAbstractClass(true);
        }
        String superClass = jc.getSuperclassName();
        ClassNode superNode = getClassNode(superClass);
        relate(superNode, node);
        String[] interfaces = jc.getInterfaceNames();
        for (int i = 0; i < interfaces.length; i++) {
            String anInterface = interfaces[i];
            ClassNode intNode = getClassNode(anInterface);
            relate(intNode, node);
        }
    }

    private void traverseDirectory(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                traverseDirectory(file);
            } else {
                if (file.getName().endsWith(".class")) {
                    indexClass(new FileInputStream(file), file.getName());
                }
            }
        }
    }

    private void traverseZipStream(ZipInputStream zip) throws IOException {
        ZipEntry zipEntry = zip.getNextEntry();
        while (zipEntry != null) {
            if (zipEntry.getName().endsWith(".class")) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte buffer[] = new byte[1024];

                for (; ;) {
                    int len = zip.read(buffer);
                    if (len < 0) {
                        break;
                    }
                    baos.write(buffer, 0, len);
                }
                ByteArrayInputStream in = new ByteArrayInputStream(baos.toByteArray());
                indexClass(in, zipEntry.getName());
            }
            zipEntry = zip.getNextEntry();
        }
    }

    private void initClassTree() {
        log.debug("Searchable Classloader is constructing its tree...");
        for (int i = 0; i < urls.length; i++) {
            URL url = urls[i];
            try {
                InputStream in = null;
                if ("file".equals(url.getProtocol())) {
                    URI uri = new URI(url.toString());
                    File file = new File(uri);
                    if (file.isDirectory()) {
                        // Option 1: Get files by traversing directory
                        traverseDirectory(file);
                        return;
                    } else {
                        in = new ZipInputStream(new FileInputStream(file));
                    }
                } else {
                    in = url.openStream();
                }
                // Option 2: Get files from zipped repository (jar or zip)
                log.debug("Input stream is a " + in.getClass().getName());
                traverseZipStream((ZipInputStream) in);
            } catch (IOException e) {
                log.warn("Could not load classes from: " + url, e);
            } catch (URISyntaxException urise) {
                log.warn("Could not convert URL to URI: " + url, urise);
            }
        }
        log.debug("Searchable Classloader has completed constructing its tree.");
    }

    private void helperAssignable(List<String> nodes, ClassNode parent, boolean concreteOnly) {
        if (parent.isLocal()) {
            if (!parent.isAbstractClass() || !concreteOnly) {
                nodes.add(parent.getClassName());
            }
        }
        Collection<ClassNode> children = parent.getAllChildren();
        if (children != null) {
            for (ClassNode child: children) {
                helperAssignable(nodes, child, concreteOnly);
            }
        }
    }

    public String[] getAllClassesAssignableTo(Class parent, boolean concreteOnly) {
        return getAllClassesAssignableTo(parent.getName(), concreteOnly);
    }

    public String[] getAllClassesAssignableTo(String parent, boolean concreteOnly) {
        ClassNode node = getClassNode(parent);
        if (node != null) {
            List<String> nodes = new ArrayList<String>();
            helperAssignable(nodes, node, concreteOnly);
            return (String[]) nodes.toArray(new String[0]);
        }
        return new String[0];
    }

    private void writeClassTreeHelper(PrintStream out, ClassNode node, int depth) {
        for (int i = 0; i < depth; i++) {
            out.print("  ");
        }
        out.println(node.getClassName());
        Collection children = node.getAllChildren();
        if (children != null) {
            for (Iterator iterator = children.iterator(); iterator.hasNext();) {
                ClassNode child = (ClassNode) iterator.next();
                writeClassTreeHelper(out, child, depth + 1);
            }
        }
    }

    public void writeClassTree(PrintStream out) {
        for (Iterator iterator = topSet.iterator(); iterator.hasNext();) {
            ClassNode node = (ClassNode) iterator.next();
            writeClassTreeHelper(out, node, 0);
        }
    }


}
