#!/bin/bash

# This script was created by Milan Dojchinovski <http://dojchinovski.mk>
# Contact me by an email at <dojcinovski.milan@gmail.com> or on Twitter: @mici
#
# Feel free to use and adapt it to your needs!
echo "##########################################################"
echo [$'\e[36mINFO\e[0m'] "Entityclassifier.eu configuration script started."
cd ..
enlhdCoreLoc=$(pwd)"/resources/lhd-2.3.9/en.LHDv1.draft.nt"
delhdCoreLoc=$(pwd)"/resources/lhd-2.3.9/de.LHDv1.draft.nt"
nllhdCoreLoc=$(pwd)"/resources/lhd-2.3.9/nl.LHDv1.draft.nt"
eninferredLoc=$(pwd)"/resources/lhd-2.3.9/en.inferredmappingstoDBpedia.nt"
deinferredLoc=$(pwd)"/resources/lhd-2.3.9/de.inferredmappingstoDBpedia.nt"
nlinferredLoc=$(pwd)"/resources/lhd-2.3.9/nl.inferredmappingstoDBpedia.nt"
salienceDatasetLoc="/resources/reuters-128-full.arff"

dbpediaOntologyLoc=$(pwd)"/resources/dbpedia-3.9/dbpedia_2014.owl"
enDisambiguationDatasetLoc=$(pwd)"/resources/dbpedia-3.9/disambiguations_en.nt"
deDisambiguationDatasetLoc=$(pwd)"/resources/dbpedia-3.9/disambiguations_de.nt"
nlDisambiguationDatasetLoc=$(pwd)"/resources/dbpedia-3.9/disambiguations_nl.nt"

enDbpediaInstancesDatasetLoc=$(pwd)"/resources/dbpedia-3.9/instance_types_en.nt"
deDbpediaInstancesDatasetLoc=$(pwd)"/resources/dbpedia-3.9/instance_types_de.nt"
nlDbpediaInstancesDatasetLoc=$(pwd)"/resources/dbpedia-3.9/instance_types_nl.nt"

yagoOntologyLoc=$(pwd)"/resources/yago/yagoTaxonomy.tsv"
yagoLabelsDatasetLoc=$(pwd)"/resources/yago/yagoTaxonomy.tsv"
yagoTypesDatasetLoc=$(pwd)"/resources/yago/yagoTypes.tsv"
yagoMultilanguageLabelsDatasetLoc=$(pwd)"/resources/yago/yagoMultilingualInstanceLabels.tsv"

enInterlangLinksDatasetLoc=$(pwd)"/resources/dbpedia-3.9/interlanguage_links_en.ttl"
deInterlangLinksDatasetLoc=$(pwd)"/resources/dbpedia-3.9/interlanguage_links_de.ttl"
nlInterlangLinksDatasetLoc=$(pwd)"/resources/dbpedia-3.9/interlanguage_links_nl.ttl"

yagoDownloadServer="http://resources.mpi-inf.mpg.de/yago-naga/yago/download/yago/"
yagoOntology="yagoTaxonomy.tsv.7z"
yagoMultilanguageLabelsDataset="yagoMultilingualInstanceLabels.tsv.7z"
yagoLabelsDataset="yagoLabels.tsv.7z"
yagoTypesDataset="yagoTypes.tsv.7z"

salienceDataset="reuters-128-full.arff"

#dbpediaDownloadServer="http://downloads.dbpedia.org/3.9/"
dbpediaDownloadServer="http://data.dws.informatik.uni-mannheim.de/dbpedia/2014/"

bitbucketDownloadServer="https://bitbucket.org/entityclassifier/entityclassifier-gate-stand-alone-plugin/raw/014957fa3e44a81ebd58e801b13e9af204a01cde/datasets/"
lhdDownloadServer="http://boa.lmcloud.vse.cz/LHD/"

dbpediaOntology="dbpedia_2014.owl"

enDisambiguationDataset="disambiguations_en.nt.bz2"
deDisambiguationDataset="disambiguations_de.nt.bz2"
nlDisambiguationDataset="disambiguations_nl.nt.bz2"

enDbpediaInstancesDataset="instance_types_en.nt.bz2"
deDbpediaInstancesDataset="instance_types_de.nt.bz2"
nlDbpediaInstancesDataset="instance_types_nl.nt.bz2"

enlhdCore="en.LHDv1.draft.nt.gz"
nllhdCore="nl.LHDv1.draft.nt.gz"
delhdCore="de.LHDv1.draft.nt.gz"

eninferred="en.inferredmappingstoDBpedia.nt"
deinferred="de.inferredmappingstoDBpedia.nt"
nlinferred="nl.inferredmappingstoDBpedia.nt"

echo [$'\e[36mINFO\e[0m'] "Compiling the Entityclassifier.eu CORE code ..."
cd ../entityclassifier-core
#mvn compile > /dev/null

cd ../entityclassifier-rest
echo [$'\e[36mINFO\e[0m'] "Compiling the Entityclassifier.eu REST code ..."
#mvn compile > /dev/null

echo [$'\e[36mINFO\e[0m'] "Started checking datasets presence ..."

# Creating resources directory "resources".
if [ ! -d "resources" ]; then
  # Check if the partitions directory exist.
  mkdir "resources"
fi

cd resources

echo [$'\e[36mINFO\e[0m'] "Checking presence of the DBpedia datasets ..."

# Creating DBpedia Ontology download directory "dbpedia-3.9".
if [ ! -d "dbpedia-3.9" ]; then
  # Check if the partitions directory exist.
  mkdir "dbpedia-3.9"
fi

cd dbpedia-3.9

#### DBpedia Ontology v3.9 ####
if [ ! -f "${dbpediaOntology:0:16}" ]; then
  #echo "Started downloading DBpedia Ontology v3.9 ..."
  curl -# -O "$dbpediaDownloadServer$dbpediaOntology"
  #echo "Finished downloading."  
  if [ ! -f "${dbpediaOntology:0:16}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "The DBpedia ontology could not be downloaded."
  else
    echo [$'\e[32mOK\e[0m'] "Downloaded DBpedia Ontology v3.9."
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found DBpedia Ontology v3.9."
fi

# DBpedia English instances
if [ ! -f "${enDbpediaInstancesDataset:0:24}" ]; then
  #echo "Started downloading DBpedia English instances ..."
  #curl -# -O "$dbpediaDownloadServer"en/"$enDbpediaInstancesDataset"
  #bzip2 -d $enDbpediaInstancesDataset
  if [ ! -f "${enDbpediaInstancesDataset:0:24}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "DBpedia English instances dataset could not be downloaded."
  else
    rm $enDbpediaInstancesDataset    
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found DBpedia Ontology v3.9."
fi

# DBpedia German instances
if [ ! -f "${deDbpediaInstancesDataset:0:24}" ]; then
  #echo "Started downloading DBpedia Ontology v3.9 ..."
  #curl -# -O "$dbpediaDownloadServer"de/"$deDbpediaInstancesDataset"
  #bzip2 -d $enDisambiguationDataset
  if [ ! -f "${deDbpediaInstancesDataset:0:24}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "DBpedia German instances dataset could not be downloaded."
  else
    rm $deDbpediaInstancesDataset    
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found DBpedia Ontology v3.9."
fi

# DBpedia Dutch instances
if [ ! -f "${nlDbpediaInstancesDataset:0:24}" ]; then
  #echo "Started downloading DBpedia Ontology v3.9 ..."
  #curl -# -O "$dbpediaDownloadServer"nl/"$nlDbpediaInstancesDataset"
  #bzip2 -d $enDisambiguationDataset
  if [ ! -f "${nlDbpediaInstancesDataset:0:24}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "DBpedia Dutch instances dataset could not be downloaded."
  else
    rm $nlDbpediaInstancesDataset
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found DBpedia Ontology v3.9."
fi

# English disambiguation dataset
if [ ! -f "${enDisambiguationDataset:0:21}" ]; then
  echo "Started downloading English disambiguation dataset ..."
  curl -# -O "$dbpediaDownloadServer"en/"$enDisambiguationDataset"
  bzip2 -d $enDisambiguationDataset
  if [ ! -f "${enDisambiguationDataset:0:21}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "DBpedia English disambiguation dataset could not be downloaded."
  else
    echo [$'\e[32mOK\e[0m'] "Downloaded English disambiguation dataset."
  fi
  echo "Finished downloading."
else
  echo [$'\e[32mOK\e[0m'] "Found English disambiguation dataset."
fi

# German disambiguation dataset
if [ ! -f "${deDisambiguationDataset:0:21}" ]; then
  curl -# -O "$dbpediaDownloadServer"de/"$deDisambiguationDataset"
  bzip2 -d $deDisambiguationDataset
  if [ ! -f "${deDisambiguationDataset:0:21}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "DBpedia German instances dataset could not be downloaded."
  else
    echo [$'\e[32mOK\e[0m'] "Downloaded German disambiguation dataset."
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found German disambiguation dataset."
fi

# Dutch disambiguation dataset
if [ ! -f "${nlDisambiguationDataset:0:21}" ]; then
  curl -# -O "$dbpediaDownloadServer"nl/"$nlDisambiguationDataset"
  bzip2 -d $nlDisambiguationDataset
  if [ ! -f "${nlDisambiguationDataset:0:25}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "DBpedia Dutch disambiguation dataset could not be downloaded."    
  else
    echo [$'\e[32mOK\e[0m'] "Downloaded Dutch disambiguation dataset."
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found Dutch disambiguation dataset."
fi

cd ..

# Creating download directory "salience".
#if [ ! -d "salience" ]; then
#  # Check if the partitions directory exist.
#  mkdir "salience"
#fi

#cd salience
#if [ ! -f "${salienceDataset:0:21}" ]; then
#  echo "Started downloading the entity salience dataset ..."
#  # TODO: update dataset download location
#  # curl -# -O "$lhdDownloadServer$enlhdCore" 
#  echo "Finished downloading."
#else
#  echo [$'\e[32mOK\e[0m'] "Found the entity salience dataset ."    
#fi
#
#cd ..

# Creating download directory "yago".
if [ ! -d "yago" ]; then
  # Check if the partitions directory exist.
  mkdir "yago"
fi

cd yago

echo [$'\e[36mINFO\e[0m'] "Checking presence of the YAGO datasets ..."

# YAGO ontology
if [ ! -f "${yagoOntology:0:16}" ]; then
  #echo "YAGO labels not found. Started downloading ..."
  # TODO: update dataset download location
  curl -# -O "$yagoDownloadServer$yagoOntology"
  #echo "Finished downloading YAGO ontology."
  7z e $yagoOntology
  rm $yagoOntology
else
  echo [$'\e[32mOK\e[0m'] "Found the YAGO ontology ."    
fi

# YAGO labels
if [ ! -f "${yagoLabelsDataset:0:14}" ]; then
  #echo "YAGO labels not found. Started downloading ..."
  curl -# -O "$yagoDownloadServer$yagoLabelsDataset" 
  #echo "Finished downloading YAGO labels."
  7z e $yagoLabelsDataset
  rm $yagoLabelsDataset
  if [ ! -f "${yagoLabelsDataset:0:14}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "YAGO labels dataset could not be downloaded."
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found the YAGO labels dataset ."    
fi

# YAGO multilingual labels
if [ ! -f "${yagoMultilanguageLabelsDataset:0:34}" ]; then
  #echo "YAGO multilingual labels not found. Started downloading ..."
  curl -# -O "$yagoDownloadServer$yagoMultilanguageLabelsDataset" 
  #echo "Finished downloading."
  7z e $yagoMultilanguageLabelsDataset
  rm $yagoMultilanguageLabelsDataset
  if [ ! -f "${yagoMultilanguageLabelsDataset:0:34}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "YAGO multilingual labels dataset could not be downloaded."
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found the YAGO multilingual labels dataset ."    
fi

# YAGO types
if [ ! -f "${yagoTypesDataset:0:13}" ]; then
  #echo "YAGO types not found. Started downloading ..."
  curl -# -O "$yagoDownloadServer$yagoTypesDataset" 
  #echo "Finished downloading."
  7z e $yagoTypesDataset
  rm $yagoTypesDataset
  if [ ! -f "${yagoTypesDataset:0:13}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "YAGO types dataset could not be downloaded."
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found the YAGO types dataset ."    
fi

cd ..

echo [$'\e[36mINFO\e[0m'] "Checking presence of the Linked Hypernyms Dataset datasets ..."

# Creating download directory "lhd-2.3.9".
if [ ! -d "lhd-2.3.9" ]; then
  # Check if the partitions directory exist.
  mkdir "lhd-2.3.9"
fi

cd lhd-2.3.9

################ Downloading Linked Hypernyms Dataset ################

### Downloading and decompressing English LHD Core partitions ###
if [ ! -f "${enlhdCore:0:17}" ]; then
  #echo "Started downloading English LHD Core files ..."
  curl -# -O "$lhdDownloadServer$enlhdCore"
  #echo "Finished downloading."
  if [ ! -f "${enlhdCore:0:17}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "English LHD Core could not be downloaded."  
  else
    echo [$'\e[32mOK\e[0m'] "Downloaded English LHD Core partition."  
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found English LHD Core partition."    
fi

if [ ! -f "${enlhdCore:0:17}" ]; then
  echo "Started decompressing English LHD Core files ..."
  gunzip $enlhdCore
  echo "Finished decompressing."
fi
##############################################################


### Downloading and decompressing German LHD Core partitions ###
if [ ! -f "${delhdCore:0:17}" ]; then
  #echo "Started downloading German LHD Core files ..."
  curl -# -O "$lhdDownloadServer$delhdCore"
  #echo "Finished downloading."
  if [ ! -f "${delhdCore:0:17}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "German LHD Core could not be downloaded."  
  else
    echo [$'\e[32mOK\e[0m'] "Downloaded German LHD Core partition."  
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found German LHD Core partition."    
fi

if [ ! -f "${delhdCore:0:17}" ]; then
  echo "Started decompressing German LHD Core files ..."
  gunzip $delhdCore
  echo "Finished decompressing."
fi
##############################################################

### Downloading and decompressing Dutch LHD Core partitions ###
if [ ! -f "${nllhdCore:0:17}" ]; then
  #echo "Started downloading Dutch LHD Core files ..."
  curl -# -O "$lhdDownloadServer$nllhdCore"
  #echo "Finished downloading."
  if [ ! -f "${nllhdCore:0:17}" ]; then
    echo [$'\e[31mNOT OK\e[0m'] "Dutch LHD Core could not be downloaded."  
  else
    echo [$'\e[32mOK\e[0m'] "Downloaded Dutch LHD Core partition."  
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found Dutch LHD Core partition."    
fi

if [ ! -f "${nllhdCore:0:17}" ]; then
  echo "Started decompressing Dutch LHD Core files ..."
  gunzip $nllhdCore
  echo "Finished decompressing."
fi
##############################################################

### Downloading English LHD v2.0 inferred types partition ###
if [ ! -f "${eninferred}" ]; then
  #echo "Started downloading the English LHD v2.0 inferred types partition ..."
  curl -# -O "$bitbucketDownloadServer$eninferred"
  if [ ! -f "${eninferred}" ]; then
    echo [$'\e[32mOK\e[0m'] "Downloaded English LHD inferred partition."    
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found English LHD inferred partition."    
fi
##############################################################

### Downloading German LHD v2.0 inferred types partition ###
if [ ! -f "${deinferred}" ]; then
  #echo "Started downloading the German LHD v2.0 inferred types partition ..."
  curl -# -O "$bitbucketDownloadServer$deinferred"
  if [ ! -f "${deinferred}" ]; then
    echo [$'\e[32mOK\e[0m'] "Downloaded German LHD inferred partition."    
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found German LHD inferred partition."    
fi
##############################################################

### Downloading Dutch LHD v2.0 inferred types partition ###
if [ ! -f "${nlinferred}" ]; then
  #echo "Started downloading the Dutch LHD v2.0 inferred types partition ..."
  curl -# -O "$bitbucketDownloadServer$nlinferred"
  if [ ! -f "${nlinferred}" ]; then
    echo [$'\e[32mOK\e[0m'] "Downloaded Dutch LHD inferred partition."    
  fi
else
  echo [$'\e[32mOK\e[0m'] "Found Dutch LHD inferred partition."    
fi
##############################################################
#echo $(pwd)
cd ../../../entityclassifier-core/src/main/resources/resources

file=settings.ini

echo [$'\e[36mINFO\e[0m'] "Checking presence of the configuration file $file ..."
#echo "checking presence of the configuration file $file ..."

if [ ! -f "$file" ]; then
  echo "#################################################################################" > $file
  echo "#                           Copyright (C) 2015                                  #" >> $file
  echo "#  This is the properties file of the Entityclassifier.eu project. It contains  #" >> $file
  echo "#  the paths to the needed resources and needed configuration information.      #" >> $file
  echo "#  -- -- -- --  -- -- -- --  -- -- -- --  -- -- -- --  -- -- -- --  -- -- -- -- #" >> $file
  echo "#  Web: http://entityclassifier.eu                                              #" >> $file
  echo "#  Author: Milan Dojchinovski - milan.dojchinovski@fit.cvut.cz                  #" >> $file
  echo "#  -- -- -- --  -- -- -- --  -- -- -- --  -- -- -- --  -- -- -- --  -- -- -- -- #" >> $file
  echo "#                                                                               #" >> $file
  echo "#        Knowledge Engineering Group (KEG) - http://keg.vse.cz/                 #" >> $file
  echo "#  -- -- -- --  -- -- -- --  -- -- -- --  -- -- -- --  -- -- -- --  -- -- -- -- #" >> $file
  echo "#                                                                               #" >> $file
  echo "#        Web Intelligence Research Group (WIRG) - http://wi.fit.cvut.cz/        #" >> $file
  echo "#################################################################################" >> $file
  echo "" >> $file
  echo "#### Number of workers - max number of concurrent processing" >> $file
  echo "workers.pool.size=1" >> $file
  echo "" >> $file
  echo "#### GATE settings" >> $file
  echo "gate.home=" >> $file
  echo "gate.plugins=" >> $file
  echo "" >> $file
  echo "#### MongoDB database settings" >> $file
  echo "mongodb.url=localhost" >> $file
  echo "mongodb.port=27017" >> $file
  echo "mongodb.database=thddb" >> $file
  echo "" >> $file
  echo "#### Part-of-speech tagger binaries for German and Dutch" >> $file
  echo "Enter the location of your German POS binaries. Note: leave it blank if you don't have one. "
  echo "Example: file:/home/user/treetagger-binaries/cmd/tree-tagger-german-utf8"
  echo -n "German POS binaries location > "
  read text
  echo "pos.tagger.binary.de="$text >> $file
  echo "Enter the location of your Dutch POS binaries. Note: leave it blank if you don't have one. "
  echo "Example: file:/home/user/treetagger-binaries/cmd/tree-tagger-dutch-utf8"
  echo -n "Dutch POS binaries location > "
  read text
  echo "pos.tagger.binary.nl="$text >> $file
  echo "" >> $file
  echo "#### Redis settings" >> $file
  echo "redis.url=localhost" >> $file
  echo "redis.port=6379" >> $file
  echo "" >> $file
  echo "#### Lucene indices endpoints" >> $file
  echo "Enter the URL of your English Lucene endpoint. Note: leave it blank if you don't have one. "
  echo -n "English Lucene endpoint URL > "
  read text
  echo "lucene.en.url="$text >> $file
  echo "Enter the URL of your German Lucene endpoint. Note: leave it blank if you don't have one. "
  echo -n "German Lucene endpoint URL > "
  read text
  echo "lucene.de.url="$text >> $file
  echo "Enter the URL of your Dutch Lucene endpoint. Note: leave it blank if you don't have one. "
  echo -n "Dutch Lucene endpoint URL > "
  read text
  echo "lucene.nl.url="$text >> $file
  echo "" >> $file
  echo "#### Locations for the entity extraction grammars" >> $file
  echo "entity.extraction.jape.grammar.en=/resources/en_entity_extraction.jape" >> $file
  echo "entity.extraction.jape.grammar.de=/resources/de_entity_extraction.jape" >> $file
  echo "entity.extraction.jape.grammar.nl=/resources/nl_entity_extraction.jape" >> $file
  echo "" >> $file
  echo "#### Locations for the hypernyms extraction grammars" >> $file
  echo "hypernym.extraction.jape.grammar.en=/resources/en_hearst_v2.jape" >> $file
  echo "hypernym.extraction.jape.grammar.de=/resources/de_hearst_v2.jape" >> $file
  echo "hypernym.extraction.jape.grammar.nl=/resources/nl_hearst_v2.jape" >> $file
  echo "" >> $file
  echo "#### DBpedia Ontology file location" >> $file
  echo "dataset.dbpedia.ontology.location="$dbpediaOntologyLoc >> $file
  echo "dataset.yago.ontology.location="$yagoOntologyLoc >> $file
  echo "" >> $file
  echo "#### Wikipedia live mirror *API* locations" >> $file
  echo "wikipedia.api.live.en=http://en.wikipedia.org/w/api.php" >> $file
  echo "wikipedia.api.live.de=http://de.wikipedia.org/w/api.php" >> $file
  echo "wikipedia.api.live.nl=http://nl.wikipedia.org/w/api.php" >> $file
  echo "" >> $file
  echo "#### Wikipedia live mirror *EXPORT API* locations" >> $file
  echo "wikipedia.export.api.live.en=http://en.wikipedia.org/wiki/Special:Export/" >> $file
  echo "wikipedia.export.api.live.de=http://de.wikipedia.org/wiki/Special:Export/" >> $file
  echo "wikipedia.export.api.live.nl=http://nl.wikipedia.org/wiki/Special:Export/" >> $file
  echo "" >> $file
  echo "#### Wikipedia local mirror *API* locations" >> $file
  echo -n "Enter the endpoint URL of your local English Wikipedia API (Note: leave it blank if you don't have one). > "
  read text
  echo "wikipedia.api.local.en="$text >> $file
  echo -n "Enter the endpoint URL of your local German Wikipedia API (Note: leave it blank if you don't have one). > "
  read text
  echo "wikipedia.api.local.de="$text >> $file
  echo -n "Enter the endpoint URL of your local Dutch Wikipedia API (Note: leave it blank if you don't have one). > "
  read text
  echo "wikipedia.api.local.nl="$text >> $file
  echo "" >> $file
  echo "#### Wikipedia local mirror *EXPORT API* locations" >> $file
  echo "wikipedia.export.api.local.en=http://ner.vse.cz/wiki/index.php/Special:Export/" >> $file
  echo "wikipedia.export.api.local.de=http://ner.vse.cz/de-wikipedia/index.php/Spezial:Exportieren/" >> $file
  echo "wikipedia.export.api.local.nl=http://ner.vse.cz/wikipedia/nl/index.php/Speciaal:Exporteren/" >> $file
  echo "" >> $file
  echo "#### Location of the entity salience dataset" >> $file
  echo "dataset.salience.reuters128="$salienceDatasetLoc >> $file
  echo "" >> $file
  echo "#### Locations of the DBpedia disambiguation datasets" >> $file
  echo "dataset.dbpedia.disambiguation.en="$enDisambiguationDatasetLoc >> $file
  echo "dataset.dbpedia.disambiguation.de="$deDisambiguationDatasetLoc >> $file
  echo "dataset.dbpedia.disambiguation.nl="$nlDisambiguationDatasetLoc >> $file
  echo "" >> $file
  echo "#### Locations of the types mappings LHD datasets" >> $file
  echo "dataset.lhd.inferrred.en="$eninferredLoc >> $file
  echo "dataset.lhd.inferrred.de="$deinferredLoc >> $file
  echo "dataset.lhd.inferrred.nl="$nlinferredLoc >> $file
  echo "" >> $file
  echo "#### Locations of the LHD Core datasets" >> $file
  echo "dataset.lhd.core.en="$enlhdCoreLoc >> $file
  echo "dataset.lhd.core.de="$delhdCoreLoc >> $file
  echo "dataset.lhd.core.nl="$nllhdCoreLoc >> $file
  echo "" >> $file
  echo "#### Location of the SemiTags endpoints" >> $file
  echo -n "SemiTags entity spotting endpoint > "
  read text
  echo "semitags.endpoint.spotting="$text >> $file
  echo -n "SemiTags entity linking endpoint > "
  read text
  echo "semitags.endpoint.linking="$text >> $file
  echo "" >> $file
  echo "" >> $file
  echo "##########################################################################" >> $file
  echo "" >> $file
  echo "dataset.yago.labels="$yagoLabelsDatasetLoc >> $file
  echo "dataset.yago.multilingual.labels="$yagoMultilanguageLabelsDatasetLoc  >> $file
  echo "dataset.yago.types="$yagoTypesDatasetLoc >> $file
  echo "" >> $file
  echo "dataset.dbpedia.instances.en="$enDbpediaInstancesDatasetLoc  >> $file
  echo "dataset.dbpedia.instances.de="$deDbpediaInstancesDatasetLoc  >> $file
  echo "dataset.dbpedia.instances.nl="$nlDbpediaInstancesDatasetLoc  >> $file
  echo "" >> $file
  echo "dataset.dbpedia.interlanguagelinks.en="$enInterlangLinksDatasetLoc  >> $file
  echo "dataset.dbpedia.interlanguagelinks.de="$deInterlangLinksDatasetLoc  >> $file
  echo "dataset.dbpedia.interlanguagelinks.nl="$nlInterlangLinksDatasetLoc  >> $file
  echo "" >> $file
  echo -n "Enter the admin API key > "
  read text
  echo "Your admin API key is: $text"
  echo "entityclassifier.apikey.admin="$text >> $file
  echo "" >> $file
  echo "" >> $file
  echo "#################################################################################"  >> $file
  echo "# This configuration file contains sensible information.                        #"  >> $file
  echo "#                                                                               #"  >> $file
  echo "#                      IT SHOULD NOT BE DISTRIBUTED!!!                          #"  >> $file
  echo "#################################################################################"  >> $file
  echo "" >> $file
  echo "############################ END OF THE DOCUMENT ################################"  >> $file

  echo [$'\e[36mOK\e[0m'] "Created empty configuration file $file !!! Please provide all required configuration information !!!"
else
  echo [$'\e[33mWARNING\e[0m'] "New configuration file settings.ini wasn't created - we noticed you have one. !!! Please check whether all required information are provided and correct !!!"
  echo [$'\e[36mINFO\e[0m'] "If you want to create new configuration file, please remove the existing one."
fi

echo [$'\e[36mINFO\e[0m'] "Finished. The datasets folder \"resources\" should have the following structure."

echo [$'\e[36mINFO\e[0m'] "resources/"
echo [$'\e[36mINFO\e[0m'] "  |__ dbpedia-3.9"
echo [$'\e[36mINFO\e[0m'] "        - dbpedia_2014.owl"
echo [$'\e[36mINFO\e[0m'] "        - disambiguations_en.nt"
echo [$'\e[36mINFO\e[0m'] "        - disambiguations_de.nt"
echo [$'\e[36mINFO\e[0m'] "        - disambiguations_nl.nt"
echo [$'\e[36mINFO\e[0m'] "  |__ lhd-2.3.9"
echo [$'\e[36mINFO\e[0m'] "        - en.LHDv1.draft.nt"
echo [$'\e[36mINFO\e[0m'] "        - de.LHDv1.draft.nt"
echo [$'\e[36mINFO\e[0m'] "        - nl.LHDv1.draft.nt"
echo [$'\e[36mINFO\e[0m'] "        - en.inferredmappingstoDBpedia.nt"
echo [$'\e[36mINFO\e[0m'] "        - de.inferredmappingstoDBpedia.nt"
echo [$'\e[36mINFO\e[0m'] "        - nl.inferredmappingstoDBpedia.nt"
echo [$'\e[36mINFO\e[0m'] "  |__ yago"
echo [$'\e[36mINFO\e[0m'] "        - yagoLabels.tsv"
echo [$'\e[36mINFO\e[0m'] "        - yagoMultilingualInstanceLabels.tsv"
echo [$'\e[36mINFO\e[0m'] "        - yagoTaxonomy.tsv"
echo [$'\e[36mINFO\e[0m'] "        - nl.inferredmappingstoDBpedia.nt"
echo [$'\e[36mINFO\e[0m'] "        - yagoTypes.tsv"











