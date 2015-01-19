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
survey6 <- surveyAll[surveyAll$ID_2 == 3 & !is.na(surveyAll$ID_2),];

# Get only select columns to be pulled in by ABM
#   Survey_number, Sex, Elementary School Code, Science Teacher Code
#   Stuff I do
#   Who Encourages You
#   Stuff that interests me
#   Earth_space_5, Human_bio_5, Tech_eng_5

# This allows me to reference the columns directly which helps with A's column names
# I had to change a couple of misspellings by hand.
# The Survey_number and Survey_number_2 are the same
# The list of activities is slightly different between 6th grade survey and 5th grade
# The Encouragement section also added a "Do Not Do" box.
attach(survey6);
A <- data.frame(Survey_number, Sex, School_code_2, Science_teacher_2,
                Library_me_2, OMSI_me_2, Zoo_me_2, Child_museum_me_2,
                Scouts_me_2, Program_me_2, National_parks_me_2,
                Team_sport_me_2, Own_sport_me_2, Summer_camp_me_2,
                Hike_outdoors_me_2, Garden_me_2, Experiments_me_2, 
                Read_me_2, Internet_me_2, Computer_me_2, 
                Communicate_me_2, TV_me_2, Build_me_2, Pets_me_2,
                Library_parents_2,        Library_sibling_2,        Library_friends_2,        Library_no_one,          Library_do_not_2,
                OMSI_parents_2,           OMSI_sibling_2,           OMSI_friends_2,           OMSI_no_one_2,           OMSI_do_not_2,
                Zoo_parents_2,            Zoo_sibling_2,            Zoo_friend_2,             Zoo_no_one_2,            Zoo_do_not_2,
                Childrens_parent_2,       Childrens_sibling_2,      Childrens_friend_2,       Childrens_no_one_2,      Childrens_do_not_2,
                Scouts_parents_2,         Scouts_sibling_2,         Scouts_friends_2,         Scouts_no_one_2,         Scouts_do_not_2,
                Other_after_parents_2,    Other_after_sibling_2,    Other_after_friend_2,     Other_after_no_one_2,    Other_after_do_not_2,
                National_park_parents_2,  National_park_sibling_2,  National_park_friends_2,  National_park_no_one_2,  National_park_do_not_2,
                Team_sport_parent_2,      Team_sport_sibling_2,	    Team_sport_friend_2,	    Team_sport_no_one_2,	   Team_sport_do_not_2,
                Sport_parent_2,           Sport_sibling_2,	        Sport_friend_2,	          Sport_no_one_2,	         Sport_do_not_2,
                Summer_camps_parent_2,    Summer_camps_sibling_2,	  Summer_camps_friends_2,	  Summer_camps_no_one_2,	 Summer_camps_do_not_2,
                Hike_outdoors_parents_2,  Hike_outdoors_siblings_2,	Hike_outdoors_friends_2,	Hike_outdoors_no_one_2,	 Hike_outdoors_do_not_2,
                Garden_parents_2,         Garden_siblings_2,	      Garden_friends_2,	        Garden_no_one_2,	       Garden_do_not_2,
                Experiment_parents_2,     Experiment_siblings_2,	  Experiment_friends_2,	    Experiment_no_one_2,	   Experiment_do_not_2,
                Read_parents_2,           Read_siblings_2,	        Read_friends_2,	          Read_no_one_2,	         Read_do_not_2,
                Internet_parent_2,        Internet_siblings_2,	    Internet_friends_2,	      Internet_no_one_2,	     Internet_do_not_2,
                Computer_games_parent_2,  Computer_games_sibling_2,	Computer_games_friend_2,	Computer_games_no_one_2, Computer_games_do_not_2,
                Communicate_parent_2,     Communicate_sibling_2,	  Communicate_friend_2,	    Communicate_no_one_2,	   Communicate_do_not_2,
                TV_parents_2,             TV_siblings_2,	          TV_friends_2,	            TV_no_one_2,	           TV_do_not_2,
                Build_parents_2,          Build_siblings_2,	        Build_friends_2,	        Build_no_one_2,	         Build_do_not_2,
                Pets_parent_2,            Pets_sibling_2,	          Pets_friend_2,	          Pets_no_one_2,	         Pets_do_not_2,
                Stars_planets_2,  Mix_materials_2,	Weather_2,	Human_body_2,	
                Traits_2,	Sudoku_2,	Fish_hunt_2,	Earthquakes_2,	
                Measure_2,	Diseases_2,	Minecraft_2,	
                Planets_space_2,	Buildings_bridges_2,	Eat_exercise_2,	
                Engines_2,	Rocks_minerals_2,	Computers_cell_phones_2,	
                Food_flowers_2,	Maps_2,	Invent_2,	Community_green_2,	
                Shapes_2,	Puzzles_2,
                Earth_space_6, Human_bio_6, Tech_eng_6);
detach(survey6);

# Normalize and rescale index values between 0 (No Interest) & 1 (Very Interested)
# Initially they scale from 1 to 5 with some NAs in there
# These are the last three columns 
A[,(ncol(A)-2):ncol(A)] <- (A[,(ncol(A)-2):ncol(A)] - 1 ) / 4;

####### DATA CLEANING #########
# Quick scan to see that all activity values are within valid range of 1-5.
for (i in 5:24)
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

# This showed that Scouts_me_2 at index 106 had a value of 0.
# Setting it to a value of 1
A$Scouts_me_2[106] = 1; # Original was 0

# Quick scan to see that all encouragement is either 0 or 1
# Let's set any NA as a 0.
for (i in 25:124)
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


# Quick scan to see that all Interests are in valid scan
for (i in 125:147)
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
# Index = 86 seems to have a lot of NA for the Interests section


# Scan the interest vector
for (i in 148:150)
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
# Index = 86 has NA values for all three indices.

####### END DATA CLEANING #########

# Keep only records that have no NA's in columns 5:24 (Stuff I Do) & 148:150 (Interest Vector).
attach(A);
JJ <- !is.na(Library_me_2) & !is.na(OMSI_me_2) & !is.na(Zoo_me_2) & !is.na(Child_museum_me_2) & !is.na(Scouts_me_2) & 
  !is.na(Program_me_2) & !is.na(National_parks_me_2) & !is.na(Team_sport_me_2) & !is.na(Own_sport_me_2) &
  !is.na(Summer_camp_me_2) & !is.na(Hike_outdoors_me_2) & !is.na(Garden_me_2) & !is.na(Experiments_me_2) &
  !is.na(Read_me_2) & !is.na(Internet_me_2) & !is.na(Computer_me_2) & !is.na(Communicate_me_2) & !is.na(TV_me_2) & 
  !is.na(Build_me_2) & !is.na(Pets_me_2) &
  !is.na(Earth_space_6) & !is.na(Human_bio_6) & !is.na(Tech_eng_6);
detach(A);

# This leaves me with 99 records out of 144
B <- A[JJ,];

# Now encode the Encouragement section
# Each youth will have a 4-digit code in this order
#  parents = 1 (not selected) or 2 (selected)
#  siblings = 1 (not selected) or 2 (selected)
#  friends = 1 (not selected) or 2 (selected)
#  no_one = 1 (not selected) or 2 (selected)
N = dim(B)[1];
Encourage = data.frame(Library_encourage=rep(0,N), OMSI_encourage=rep(0,N), Zoo_encourage=rep(0,N), Childrens_encourage=rep(0,N), 
                       Scouts_encourage=rep(0,N), Other_after_encourage=rep(0,N), National_park_encourage=rep(0,N), 
                       Team_sport_encourage=rep(0,N), Sport_encourage=rep(0,N), Summer_camps_encourage=rep(0,N), 
                       Hike_outdoors_encourage=rep(0,N), Garden_encourage=rep(0,N), Experiment_encourage=rep(0,N),
                       Read_encourage=rep(0,N), Internet_encourage=rep(0,N), Computer_games_encourage=rep(0,N), 
                       Communicate_encourage=rep(0,N), TV_encourage=rep(0,N), Build_encourage=rep(0,N), Pets_encourage=rep(0,N));
for (j in 1:dim(Encourage)[2])
{
  i = (j-1)*5 + 25;  # To get the column in B that I want. Each encouragement has 5 elements (parents, sibling, friend, no_one, do_not)
  Encourage[,j] = (B[,i]+1)*1000 + (B[,i+1]+1)*100 + (B[,i+2]+1)*10 + (B[,i+3]+1);  
}

# Now to combine the B dataframe (minus all the encouragement stuff) with Encourage
attach(B); 
attach(Encourage);
BB <- data.frame(Survey_number, Sex, School_code_2, Science_teacher_2,
                 Library_me_2, OMSI_me_2, Zoo_me_2, Child_museum_me_2, Scouts_me_2,
                 Program_me_2, National_parks_me_2, Team_sport_me_2, Own_sport_me_2,
                 Summer_camp_me_2, Hike_outdoors_me_2, Garden_me_2, Experiments_me_2,
                 Read_me_2, Internet_me_2, Computer_me_2, Communicate_me_2, TV_me_2,
                 Build_me_2, Pets_me_2,
                 Library_encourage, OMSI_encourage, Zoo_encourage, Childrens_encourage, Scouts_encourage,
                 Other_after_encourage, National_park_encourage, Team_sport_encourage, Sport_encourage,
                 Summer_camps_encourage, Hike_outdoors_encourage, Garden_encourage, Experiment_encourage,
                 Read_encourage, Internet_encourage, Computer_games_encourage, Communicate_encourage, TV_encourage,
                 Build_encourage, Pets_encourage,
                 Stars_planets_2, Mix_materials_2, Weather_2, Human_body_2, Traits_2,
                 Sudoku_2, Fish_hunt_2, Earthquakes_2, Measure_2, Diseases_2,
                 Minecraft_2, Planets_space_2, Buildings_bridges_2, Eat_exercise_2, Engines_2,
                 Rocks_minerals_2, Computers_cell_phones_2, Food_flowers_2, Maps_2, Invent_2,
                 Community_green_2, Shapes_2, Puzzles_2,
                 Earth_space_6, Human_bio_6, Tech_eng_6);
detach(B);
detach(Encourage);

# Now I want to duplicate some of the records to get back to 144
I <- sample.int(nrow(BB), size = nrow(A)-nrow(BB));
C <- data.frame(BB[I,]);  # Duplicated records
# Change the survey number so that it stays unique.  I'm going to multiply by 1000;
C$Survey_number <- C$Survey_number*1000;

# Final data for outputing to csv file for ABM.
# Take original data BB and append resampled records in C.
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
