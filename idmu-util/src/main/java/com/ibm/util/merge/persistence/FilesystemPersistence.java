package com.ibm.util.merge.persistence;

import com.google.gson.JsonSyntaxException;
import com.ibm.util.merge.template.Template;
import com.ibm.idmu.api.JsonProxy;
import com.ibm.util.merge.storage.TarFileWriter;
import com.ibm.util.merge.storage.ZipFileWriter;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FilesystemPersistence {

    private Logger log = Logger.getLogger(FilesystemPersistence.class);

    private final File templateFolder;

    private final File outputRoot;

    private final JsonProxy jsonProxy;

    public FilesystemPersistence(File templateFolder, JsonProxy jsonProxy, File outputRoot) {
        if(templateFolder == null) throw new IllegalArgumentException("Missing templateFolder");
        if(outputRoot== null) throw new IllegalArgumentException("Missing outputRoot");
        if(jsonProxy == null) throw new IllegalArgumentException("Missing jsonProxy");
        this.templateFolder = templateFolder;
        this.jsonProxy = jsonProxy;
        this.outputRoot = outputRoot;
        if (!Files.exists(outputRoot.toPath())) {
            throw new OutputDirectoryPathDoesNotExistException(outputRoot.getAbsolutePath());
        }
        if (!Files.isDirectory(outputRoot.toPath())) {
            throw new NonDirectoryAtOutputDirectoryPathException(outputRoot.getAbsolutePath());
        }
    }

    /**********************************************************************************
     * Cache JSON templates found in the template folder.
     * Note: The template folder is initialized from Merge.java from the web.xml value  for
     * merge-templates-folder, if it is not initilized the default value is /tmp/templates
     *
     * @param folder that contains template files
     */
    public List<Template> loadAll() {
        List<Template> templates = new ArrayList<>();
        int count = 0;
        if (templateFolder.listFiles() == null) {
            throw new RuntimeException("Template Folder data was not found! " + templateFolder);
        }
        for (File file : templateFolder.listFiles()) {
            log.debug("Inspect potential template file: " + file);
            if (!file.isDirectory()) {
                try {
                    String json = String.join("\n", Files.readAllLines(file.toPath()));
                    Template template = jsonProxy.fromJSON(json, Template.class);
                    templates.add(template);
                    log.info("Loaded template " + template.getFullName() + " : " + file.getAbsolutePath());
                    count++;
                } catch (JsonSyntaxException e) {
                    log.warn("Malformed JSON Template:" + file.getName(), e);
                } catch (FileNotFoundException e) {
                    log.info("Moving on after file read error on " + file.getName(), e);
                } catch (IOException e) {
                    log.warn("IOException Reading:" + file.getName(), e);
                }
            }


        }
        log.info("Loaded " + Integer.toString(count) + " templates from " + templateFolder);
        return templates;
    }

    /**********************************************************************************
     * save provided template to the Template Folder as JSON, and add it to the Cache
     *
     * @param Template template the Template to save
     * @return a cloned copy of the Template ready for Merge Processing
     */
    public Template saveTemplateToJsonFolder(Template template) {
        String fileName = templateFolder + File.separator + template.getFullName();
        File file = new File(fileName);
        BufferedWriter bw = null;
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            File path = file.getAbsoluteFile();
            log.info("Saving " + template.getFullName() + " to "+ path.getAbsolutePath());
            FileWriter fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            bw.write(jsonProxy.toJson(template));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write template " +template.getFullName()+" to JSON folder : " + file.getPath(), e);
        } finally {
            IOUtils.closeQuietly(bw);
        }
        return template;
    }

    public void deleteTemplateOnFilesystem(Template template) {
        File file = new File(templateFolder, template.getFullName()+".json");
        if(file.exists()){
            if(!file.delete()){
                throw new RuntimeException("Could not delete template " + template.getFullName() + " at path " + file.getAbsolutePath());
            }else{
                log.info("Deleted template " + template.getFullName() + " at path " + file.getAbsolutePath());
            }
        }else{
            log.error("File for template " + template.getFullName() + " does not exist at path " + file.getAbsolutePath());
        }
    }

    public void writeTarFile(Template template) {
        File outputFilePath = constructArchivePath(template);
        if(!outputFilePath.getParentFile().exists()){
            throw new IllegalArgumentException("The parent directory does not exist for file " + outputFilePath.getAbsolutePath());
        }
        String archiveEntryName = constructArchiveEntryName(template);
        try {
            new TarFileWriter(outputFilePath, archiveEntryName, template.getContent(), "root", "root").write();
        } catch (IOException e) {
            throw new TemplatePersistenceException(template, outputFilePath, archiveEntryName, e);
        }
    }

    public String constructArchiveEntryName(Template template) {
        return template.replaceProcess(template.getOutputFile());
    }

    public void writeZipFile(Template template) {
        File outputFilePath = constructArchivePath(template);
        String archiveEntryName = constructArchiveEntryName(template);
        try {
            new ZipFileWriter(outputFilePath, archiveEntryName, template.getContent()).write();
        } catch (IOException e) {
            throw new TemplatePersistenceException(template, outputFilePath, archiveEntryName, e);
        }
    }

    public File constructArchivePath(Template template) {
        return new File(outputRoot + "/" + template.getOutputFile());
    }

    public void doWrite(Template template) {
        if (template.canWrite()) {
            if (template.getOutputType() == Template.TYPE_ZIP) {
                writeZipFile(template);
            } else {
                writeTarFile(template);
            }
        }
    }

    public static class TemplatePersistenceException extends RuntimeException {
        private final Template template;
        private final File outputFilePath;
        private final String archiveEntryName;

        public TemplatePersistenceException(Template template, File outputFilePath, String archiveEntryName, IOException e) {
            super("Error persisting template " + template.getFullName() + " to zip file at " + outputFilePath + " with entry name " + archiveEntryName, e);
            this.template = template;
            this.outputFilePath = outputFilePath;
            this.archiveEntryName = archiveEntryName;
        }

        public Template getTemplate() {
            return template;
        }

        public File getOutputFilePath() {
            return outputFilePath;
        }

        public String getArchiveEntryName() {
            return archiveEntryName;
        }
    }

    public static class OutputDirectoryPathDoesNotExistException extends RuntimeException {
        private String outputDirPath;

        public OutputDirectoryPathDoesNotExistException(String outputDirPath) {
            super("The output directory does not exist: " + outputDirPath);
            this.outputDirPath = outputDirPath;
        }

        public String getOutputDirPath() {
            return outputDirPath;
        }
    }

    public static class NonDirectoryAtOutputDirectoryPathException extends RuntimeException {
        private String outputDirPath;

        public NonDirectoryAtOutputDirectoryPathException(String outputDirPath) {
            super("Path does not denote a directory: " + outputDirPath);
            this.outputDirPath = outputDirPath;
        }

        public String getOutputDirPath() {
            return outputDirPath;
        }
    }
}