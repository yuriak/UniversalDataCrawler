package cn.edu.dufe.dufedata.util;
import java.io.BufferedInputStream;  
import java.io.ByteArrayInputStream;  
import java.io.ByteArrayOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.util.ArrayList;
import java.util.HashMap;  
import java.util.Iterator;  
import java.util.jar.JarEntry;  
import java.util.jar.JarInputStream;  
import java.util.jar.Manifest;  

import javax.sound.midi.MidiChannel;

import org.apache.commons.io.FileUtils;

public class JarFileReader {  
    private JarInputStream jarInput;  
    private HashMap<String, ByteArrayOutputStream> entriesStreamMap;  
    public JarFileReader(InputStream in) throws IOException {  
        jarInput = new JarInputStream(in);  
        entriesStreamMap = new HashMap<String, ByteArrayOutputStream>();  
    }  
      
    public ArrayList<File> readClass() throws IOException {
    	ArrayList<File> classFiles=new ArrayList<>();
        JarEntry entry = jarInput.getNextJarEntry();  
        String manifestEntry = null;  
        while(entry != null) {  
            if(entry.getName().endsWith(".class")) {  
//                copyInputStream(jarInput, entry.getName());
                ByteArrayOutputStream _copy = new ByteArrayOutputStream();  
                int read = 0;  
                int chunk = 0;  
                byte[] data = new byte[256];  
                while(-1 != (chunk = jarInput.read(data)))  
                {  
                    read += data.length;  
                    _copy.write(data, 0, chunk);  
                }
                String classFullName=entry.getName().replaceAll("/", ".");
                File file=new File("classFile/"+classFullName);
                FileUtils.writeByteArrayToFile(file, _copy.toByteArray());
                classFiles.add(file);
            }  
            entry = jarInput.getNextJarEntry();
        } 
        return classFiles;
    }  
      
    public void copyInputStream(InputStream in, String entryName) throws IOException {  
        if(!entriesStreamMap.containsKey(entryName)) {  
            ByteArrayOutputStream _copy = new ByteArrayOutputStream();  
            int read = 0;  
            int chunk = 0;  
            byte[] data = new byte[256];  
            while(-1 != (chunk = in.read(data)))  
            {  
                read += data.length;  
                _copy.write(data, 0, chunk);  
            }  
            entriesStreamMap.put(entryName, _copy);  
        }  
    }  
      
    public InputStream getCopy(String entryName) {  
        ByteArrayOutputStream _copy = entriesStreamMap.get(entryName);  
        return (InputStream)new ByteArrayInputStream(_copy.toByteArray());  
    }  
      
//    public static void main(String[] args) {  
//        File jarFile = new File("D:\\agriculture\\clickremoval.jar");  
//        try {  
//            InputStream in = new BufferedInputStream(new FileInputStream(jarFile));  
//            JarFileReader reader = new JarFileReader(in);  
//            reader.readEntries();  
//        } catch (IOException e) {  
//            e.printStackTrace();  
//        }  
//    }  
      
}  
