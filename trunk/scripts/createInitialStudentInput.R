# Reading in the survey data provided by SYNERGIES
# May_2014_-7th Grade/Survey_May_2014_ABM.sav provided by Synergies.
# This redoes the 5th grade PCA indices using the weights from 6th grade analysis.
# Indices are now the following
#
# 5th Grade         6th Grade       7th Grade
#  Earth_space_5    Earth_space_6    Earth_space_7
#  Human_bio_5      Human_bio_6      Human_bio_7
#  Tech_eng_5       Tech_eng_6       Tech_eng_7
#                   Math_6           Math_7
# I access SPSS 22 and did a "Save As" csv file using both the 
# "Using data values" & "Using label values" results.
# For the most part I want to use the data values file.
# I use my removeCtrlM.pl program to remove the ^M from csv files.
setwd("/Users/mhendrey/Documents/Eclipse/workspace/StemStudents/data/");
surveyAll <- read.csv("original/survey_may_2014_abm_datavaluesRemoveCtrlM.dat", sep='\t', stringsAsFactors=FALSE);

# To grab only the 5th grade surveys, ID == 1 (174 of these)
# If I wanted the 6th grade surveys, ID_2 == 3 (144 of these)
# If I wanted the 7th grade surveys, ID_3 == 4 (156 of these)
survey5 <- surveyAll[surveyAll$ID == 1 & !is.na(surveyAll$ID),];

# Get only select columns to be pulled in by ABM
#   Survey_number, Sex, Elementary School Code, Science Teacher Code
#   Stuff I do
#   Who Encourages You
#   Stuff that interests me
#   Earth_space_5, Human_bio_5, Tech_eng_5

# This allows me to reference the columns directly which helps with A's column names
# I had to change a couple of misspellings by hand.
attach(survey5);
A <- data.frame(Survey_number, Sex, School_code, Teacher_code,
                Library_me, Museum_me, Scouts_me, National_Parks_me,
                Afterschool_me, Talk_me, Summer_camp_me,
                Hike_outdoors_me, Garden_me, Experiments_me, Read_me,
                Internet_me, Computer_me, TV_me, Build_me,
                Library_parent,       Library_sibling,       Library_friends,       Library_no_one,
                Museum_parent,        Museum_siblings,        Museum_friends,        Museum_no_one,
                Scouts_parents,        Scouts_siblings,        Scouts_friends,        Scouts_no_one,
                National_park_parents, National_park_siblings, National_park_friends, National_park_no_one,
                Afterschool_parents,   Afterschool_siblings,   Afterschool_friends,   Afterschool_no_one,
                Talk_parents,          Talk_siblings,          Talk_friends,          Talk_no_one,
                Summer_camps_parents,  Summer_camps_siblings,  Summer_camps_friends,  Summer_camps_no_one,
                Hike_outdoors_parents, Hike_outdoors_siblings, Hike_outdoors_friends, Hike_outdoor_no_one,
                Garden_parents,        Garden_siblings,        Garden_friends,        Garden_no_one,
                Experiment_parents,    Experiment_siblings,    Experiment_friends,    Experiment_no_one,
                Read_parents,          Read_siblings,          Read_friends,          Read_no_one,
                Internet_parent,      Internet_siblings,      Internet_friends,      Internet_no_one,
                Computer_parents,      Computer_siblings,      Computer_friends,      Computer_no_one,
                TV_parents,            TV_siblings,            TV_friends,            TV_no_one,
                Build_parents,         Build_sibings,          Build_friends,         Build_no_one,
                Stars_planets, mix_materials, Weather, Human_body,
                Traits, Dinosaurs, Fish_hunt, Earthquakes,
                Instruments, Diseases, Dangerous_plants_animals,
                Planets_space, Buildings_bridges, Eat_exercise,
                Engines, Rocks_minerals, Computers_cell_phones,
                Puzzels_math, Food_flowers, Maps, Invent,
                Community_green, Pets,
                Earth_space_5, Human_bio_5, Tech_eng_5);
detach(survey5);

# Normalize and rescale index values between 0 (No Interest) & 1 (Very Interested)
# Initially they scale from 1 to 5 with some NAs in there
# These are the last three columns 
A[,(ncol(A)-2):ncol(A)] <- (A[,(ncol(A)-2):ncol(A)] - 1 ) / 4;

####### DATA CLEANING #########
# Quick scan to see that all activity values are within valid range of 1-5.
for (i in 5:19)
{
  xmin = min(A[,i], na.rm=TRUE);
  xmax = max(A[,i], na.rm=TRUE);
  if (xmin < 1)
  {
    print(c(names(A)[i],'is below minimun at index'));
    indexValue <- match(xmin,A[,i]);
    print(c(indexValue, xmin));    
  }
  if (xmax > 5)
  {
    print(c(names(A)[i],'is above maximum at index'));
    indexValue <- match(xmax,A[,i]);
#    badRecord <- A[,i] == max(A[,i],na.rm=TRUE) & !is.na(A[,i]);
    print(c(indexValue,xmax));
  }
}
# This showed that Museum_me at index 40 had a value of 54.
# Setting it to a value of 4
A$Museum_me[40] = 4; # Original was 54

# Quick scan to see that all encouragement is either 0 or 1
# Let's set any NA as a 0.
for (i in 20:79)
{
  xmin = min(A[,i], na.rm=TRUE);
  xmax = max(A[,i], na.rm=TRUE);
  if (xmin < 0)
  {
    print(c(names(A)[i],'is below minimun at index'));
    indexValue <- match(xmin,A[,i]);
    print(c(indexValue, xmin));    
  }
  if (xmax > 1)
  {
    print(c(names(A)[i],'is above maximum at index'));
    indexValue <- match(xmax,A[,i]);
    #    badRecord <- A[,i] == max(A[,i],na.rm=TRUE) & !is.na(A[,i]);
    print(c(indexValue,xmax));
  }  
  badRecord <- is.na(A[,i]);
  A[badRecord,i] = 0;
}

# This gave some values that exceeeded the maximum of 1
A$Library_parent[25] = 1;       # Original was 2
A$Museum_parent[93] = 1;        # Original was 3
A$Scouts_parents[93] = 1;       # Original was 2
A$Afterschool_parents[140] = 1; # Original was 2

# Quick scan to see that all Interests are in valid scan
for (i in 80:102)
{
  xmin = min(A[,i], na.rm=TRUE);
  xmax = max(A[,i], na.rm=TRUE);
  if (xmin < 1)
  {
    print(c(names(A)[i],'is below minimun at index'));
    indexValue <- match(xmin,A[,i]);
    print(c(indexValue, xmin));    
  }
  if (xmax > 5)
  {
    print(c(names(A)[i],'is above maximum at index'));
    indexValue <- match(xmax,A[,i]);
    print(c(indexValue,xmax));
  } 
  indexNA <- match(NA,A[,i]);
  print(c(names(A)[i],'has NA at index values'));
  print(indexNA);
}

# Scan the interest vector
for (i in 103:105)
{
  xmin = min(A[,i],na.rm=TRUE);
  xmax = max(A[,i],na.rm=TRUE);
  if (xmin < 0)
  {
    print(c(names(A)[i], 'is below minimum at index'));
    indexValue <- match(xmin,A[,i]);
    print(c(indexValue,xmin));
  }

  if (xmax > 1)
  {
    print(c(names(A)[i], 'is below minimum at index'));
    indexValue <- match(xmin,A[,i]);
    print(c(indexValue,xmin));
  }  
  indexNA <- match(NA,A[,i]);
  print(c(names(A)[i],'has NA at index values'));
  print(indexNA);
}
####### END DATA CLEANING #########

# Keep only records that have no NA's in columns 5:19 (Stuff I Do) & 103:105 (Interest Vector).
attach(A);
JJ <- !is.na(Library_me) & !is.na(Museum_me) & !is.na(Scouts_me) & !is.na(National_Parks_me) & !is.na(Afterschool_me) &
  !is.na(Talk_me) & !is.na(Summer_camp_me) & !is.na(Hike_outdoors_me) & !is.na(Garden_me) & !is.na(Experiments_me) &
  !is.na(Read_me) & !is.na(Internet_me) & !is.na(Computer_me) & !is.na(TV_me) & !is.na(Build_me) &
  !is.na(Earth_space_5) & !is.na(Human_bio_5) & !is.na(Tech_eng_5);
detach(A);

# This leaves me with 129 records out of 174
B <- A[JJ,];

# Now encode the Encouragement section
# Each youth will have a 4-digit code in this order
#  parents = 1 (not selected) or 2 (selected)
#  siblings = 1 (not selected) or 2 (selected)
#  friends = 1 (not selected) or 2 (selected)
#  no_one = 1 (not selected) or 2 (selected)
N = dim(B)[1];
Encourage = data.frame(Library_encourage=rep(0,N), Museum_encourage=rep(0,N), Scouts_encourage=rep(0,N), 
                       National_park_encourage=rep(0,N), Afterschool_encourage=rep(0,N), 
                       Talk_encourage=rep(0,N), Summer_camps_encourage=rep(0,N), 
                       Hike_outdoors_encourage=rep(0,N), Garden_encourage=rep(0,N), Experiment_encourage=rep(0,N),
                       Read_encourage=rep(0,N), Internet_encourage=rep(0,N), Computer_encourage=rep(0,N), 
                       TV_encourage=rep(0,N), Build_encourage=rep(0,N));
for (j in 1:dim(Encourage)[2])
{
  i = (j-1)*4 + 20;  # To get the column in B that I want
  Encourage[,j] = (B[,i]+1)*1000 + (B[,i+1]+1)*100 + (B[,i+2]+1)*10 + (B[,i+3]+1);  
}

# Now to combine the B dataframe (minus all the encouragement stuff) with Encourage
attach(B); 
attach(Encourage);
BB <- data.frame(Survey_number, Sex, School_code, Teacher_code,
                 Library_me, Museum_me, Scouts_me, National_Parks_me,
                 Afterschool_me, Talk_me, Summer_camp_me,
                 Hike_outdoors_me, Garden_me, Experiments_me, Read_me,
                 Internet_me, Computer_me, TV_me, Build_me,
                 Library_encourage, Museum_encourage, Scouts_encourage, 
                 National_park_encourage, Afterschool_encourage, 
                 Talk_encourage, Summer_camps_encourage, 
                 Hike_outdoors_encourage, Garden_encourage, Experiment_encourage,
                 Read_encourage, Internet_encourage, Computer_encourage, 
                 TV_encourage, Build_encourage,
                 Stars_planets, mix_materials, Weather, Human_body,
                 Traits, Dinosaurs, Fish_hunt, Earthquakes,
                 Instruments, Diseases, Dangerous_plants_animals,
                 Planets_space, Buildings_bridges, Eat_exercise,
                 Engines, Rocks_minerals, Computers_cell_phones,
                 Puzzels_math, Food_flowers, Maps, Invent,
                 Community_green, Pets,
                 Earth_space_5, Human_bio_5, Tech_eng_5);
detach(B);
detach(Encourage);

# Now I want to duplicate some of the records to get back to 174
I <- sample.int(nrow(BB), size = nrow(A)-nrow(BB));
C <- data.frame(BB[I,]);  # Duplicated records
# Change the survey number so that it stays unique.  I'm going to multiply by 1000;
C$Survey_number <- C$Survey_number*1000;

# Final data for outputing to csv file for ABM.
# Take original data B and append resampled records in C.
D <- rbind(BB,C);

filename <- "initialStudentInput.csv"
write.table(D, filename, quote=FALSE, sep=',', row.names=FALSE);

# TODO:  Rewrite so I can just use the filename variable.
#Remove blanks at end of string columns.  Need to escape the \, so use \\
system("perl -p -i -e 's/\\s+,/,/g' initialStudentInput.csv");
#Remove blanks at end of line
system("perl -p -i -e 's/\\s+$/\\n/g' initialStudentInput.csv");
#Add NA to end of line if missing data
system("perl -p -i -e 's/,$/,NA/g' initialStudentInput.csv");
#Insert NA into blank fields
system("perl -p -i -e 's/,,/,NA,/g' initialStudentInput.csv");
#Needs to be run at least twice if ,,,
system("perl -p -i -e 's/,,/,NA,/g' initialStudentInput.csv");
system("perl -p -i -e 's/,,/,NA,/g' initialStudentInput.csv");
system("perl -p -i -e 's/,,/,NA,/g' initialStudentInput.csv");
