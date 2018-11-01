package edu.columbia.historylab.ner.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.columbia.historylab.ner.constants.Constants;
import edu.columbia.historylab.ner.constants.MentionType;
import edu.columbia.historylab.ner.constants.NERType;

/*
 * Reads and records configuration settings.
 */
public class Config {
	
	private static Config instance;
	private String modelPath;
	private String gloveModelPath;
	private String resourcesPath;
	private String documentPath;

	private String outputPath;

	private String databaseUrl;
	private String databaseName;
	private String databaseUsername;
	private String databasePassword;
	
	private String trainDataDir;
	private String devDataDir;
	private String testDataDir;

	private String classifier;
	private int gloveSize;
	private int windowSize;
	private boolean includeMentionType;
	private boolean runBaseline;
	private boolean caseless;
	private int offset;
	private int limit;
	private int numFolds;
	private boolean includeNER = false;
	private boolean ignorePronouns=false;
	private boolean weightedTraining=false;
	private boolean verbose=false;
	private Map<MentionType, NERType> mentionTypeToNERTypeForceMap = new HashMap<MentionType, NERType>();
	
	/**
	 * Returns instance using config @ specified config path.
	 */
	public static Config getInstance(String configPath) {
		if(instance == null){
			if (configPath==null) {
				configPath="conf/hl.config";
			}
			instance = new Config(configPath);
		}
		return instance;
	}
	
	/**
	 * Returns instance. If configPath has never been specified, defaults to "conf/hl.config"
	 */
	public static Config getInstance() {
		return Config.getInstance(null);
	}
	
	private String getPathProperty(String path, Properties properties) throws IllegalArgumentException {
		path = properties.getProperty(path);
		if (!new File(path).exists()) {
			throw new IllegalArgumentException(path + " does not exist. All config directories and files must exist");
		}
		return path;
	}
	
	private void mkdirsForDirectoryProperty(String path, Properties properties) {
		path = properties.getProperty(path);
		new File(path).mkdirs();
	}
	
	private Config(String configPath) {
		try {			
			Properties properties = new Properties();
			FileInputStream is = new FileInputStream(configPath);
			properties.load(is);
						
			modelPath = getPathProperty(Constants.PARAMETER_MODEL_PATH, properties);
			gloveModelPath = getPathProperty(Constants.PARAMETER_GLOVE_MODEL_PATH, properties); 
			resourcesPath = getPathProperty(Constants.PARAMETER_RESOURCES_PATH, properties);
			documentPath = getPathProperty(Constants.PARAMETER_DOCUMENT_PATH, properties);
			mkdirsForDirectoryProperty(Constants.PARAMETER_OUTPUT_PATH, properties);
			outputPath = getPathProperty(Constants.PARAMETER_OUTPUT_PATH, properties);

			databaseUrl = properties.getProperty(Constants.PARAMETER_DATABASE_URL);
			databaseName = properties.getProperty(Constants.PARAMETER_DATABASE_NAME);
			databaseUsername = properties.getProperty(Constants.PARAMETER_DATABASE_USERNAME);
			databasePassword = properties.getProperty(Constants.PARAMETER_DATABASE_PASSWORD);

			trainDataDir = getPathProperty(Constants.PARAMETER_TRAIN_DATA_DIR, properties);
			devDataDir = getPathProperty(Constants.PARAMETER_DEV_DATA_DIR, properties);
			testDataDir = getPathProperty(Constants.PARAMETER_TEST_DATA_DIR, properties);

			classifier = properties.getProperty(Constants.PARAMETER_CLASSIFIER);
			gloveSize = Integer.parseInt(properties.getProperty(Constants.PARAMETER_GLOVE_SIZE));
			numFolds = Integer.parseInt(properties.getProperty(Constants.PARAMETER_NUM_FOLDS));
			windowSize = Integer.parseInt(properties.getProperty(Constants.PARAMETER_WINDOW_SIZE));
			includeMentionType = new Boolean(properties.getProperty(Constants.PARAMETER_INCLUDE_MENTION_TYPE).toLowerCase());
			runBaseline = new Boolean(properties.getProperty(Constants.PARAMETER_RUN_BASELINE).toLowerCase());
					
			caseless = Boolean.getBoolean(properties.getProperty(Constants.PARAMETER_CASELESS));
			offset = Integer.parseInt(properties.getProperty(Constants.PARAMETER_OFFSET));
			limit = Integer.parseInt(properties.getProperty(Constants.PARAMETER_LIMIT));
			includeNER = Boolean.getBoolean(properties.getProperty(Constants.PARAMETER_INCLUDE_NER));
			ignorePronouns = Boolean.getBoolean(properties.getProperty(Constants.PARAMETER_IGNORE_PRONOUNS));

			weightedTraining = Boolean.getBoolean(properties.getProperty(Constants.PARAMETER_WEIGHTED_TRAINING));
			
		
		} catch(Exception e) {
			throw new RuntimeException("Problems reading config file", e);
		}
	}
	
	public int getNumFolds() {
		return this.numFolds;
	}
	
	public boolean getWeightedTraining() {
		return this.weightedTraining;
	}
	
	public String getTrainDataDir() {
		return this.trainDataDir;
	}
	public void setTrainDataDir(String dir) {
		this.trainDataDir = dir;
	}
	
	public void addMentionTypesToNERTypeForceMap(String mappings) {
		for (String m:mappings.split(",")) {
			String[] parts = m.trim().split(">");
			this.addMentionTypeToNERTypeForceMap(MentionType.create(parts[0]), NERType.create(parts[1]));
		}
	}
	
	public boolean hasForcedMentionTypeMappings() {
		return this.mentionTypeToNERTypeForceMap.size()>0;
	}
	
	public Set<MentionType> getForcedMentionTypeMappings() {
		return this.mentionTypeToNERTypeForceMap.keySet();
	}
	
	public NERType getForcedNERTypeMapping(MentionType mt) {
		return this.mentionTypeToNERTypeForceMap.get(mt);
	}
	
	public void addMentionTypeToNERTypeForceMap(MentionType mention, NERType nERType) {
		this.mentionTypeToNERTypeForceMap.put(mention, nERType);
	}
	
	public String getDevDataDir() {
		return this.devDataDir;
	}
	public void setDevDataDir(String dir) {
		this.devDataDir = dir;
	}

	public String getTestDataDir() {
		return this.testDataDir;
	}
	public void setTestDataDir(String dir) {
		this.testDataDir = dir;
	}

	public String getModelPath() {
		return modelPath;
	}
	
	public void setModelPath(String path) {
		this.modelPath=path;
	}
	
	public String getGloveModelPath() {
		return gloveModelPath;
	}
	
	public String getGloveModel() {
		return getGloveModelPath()+"/"+Constants.GLOVE_MODEL;
	}
	
	public String getTrainData() {
		return getModelPath()+"/"+Constants.TRAIN_DATA;
	}
	
	public String getTrainModel() {
		return getModelPath()+"/"+Constants.TRAIN_MODEL;
	}
	
	public String getBaselineModel() {
		return getModelPath()+"/"+Constants.BASELINE_MODEL;
	}
	
	public String getStanfordPOSModel() {
		return "edu/stanford/nlp/models/pos-tagger/english-left3words/"+Constants.STANFORD_POS_MODEL;
	}
	
	public String getStanfordPOSCaselessModel() {
		return "edu/stanford/nlp/models/pos-tagger/"+Constants.STANFORD_POS_MODEL_CASELESS;
	}
	
	public String getStanfordParseModel() {
		return "edu/stanford/nlp/models/lexparser/"+Constants.STANFORD_PARSE_MODEL;
	}
	
	public String getStanfordParseCaselessModel() {
		return "edu/stanford/nlp/models/lexparser/"+Constants.STANFORD_PARSE_MODEL_CASELESS;
	}
	
	public String getStanfordNERModel() {
		return "edu/stanford/nlp/models/ner/"+Constants.STANFORD_NER_MODEL;
	}
	
	public String getStanfordNERCaselessModel() {
		return "edu/stanford/nlp/models/ner/"+Constants.STANFORD_NER_MODEL_CASELESS;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose=verbose;
	}

	public String getResourcesPath() {
		return resourcesPath;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public String getDatabaseUrl() {
		return databaseUrl;
	}

	public String getDatabaseName() {
		return databaseName;
	}
	
	public String getDatabaseUsername() {
		return databaseUsername;
	}
	
	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setGloveSize(int gs) {
		this.gloveSize=gs;
	}
	
	public int getGloveSize() {
		return gloveSize;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public boolean getIncludeMentionType() {
		return includeMentionType;
	}

	public boolean isRunBaseline() {
		return runBaseline;
	}

	public boolean getCaseless() {
		return caseless;
	}

	public int getOffset() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setCaseless(boolean caseless) {
		this.caseless = caseless;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void setIncludeNER(boolean includeNER) {
		this.includeNER=includeNER;
	}
	
	public boolean includeNER() {
		return this.includeNER;
	}
	
	public void setIgnorePronouns(boolean ignorePronouns) {
		this.ignorePronouns=ignorePronouns;
	}
	
	public boolean ignorePronouns() {
		return this.ignorePronouns;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
	
}
