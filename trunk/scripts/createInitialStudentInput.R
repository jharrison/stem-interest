# Reading in the survey data provided by SYNERGIES
# May_2014_-7th Grade/Survey_May_2014_ABM.sav provided by Synergies.
# Starting with the 6th grade data for model initializaiton
# Using the 4-component interest vector
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
#survey6 <- surveyAll[surveyAll$ID_2 == 3 & !is.na(surveyAll$ID_2),];
survey67 <- surveyAll[surveyAll$ID_2==3 & !is.na(surveyAll$ID_2) & surveyAll$ID_3==4 & !is.na(surveyAll$ID_3),];

# Keep only those records where the activities and 4-vector interest don't have any NA.
# Out of the 114 records that filled in botht the 6th & 7th grade survey
#    77 records had no NAs for activities or 4-vector interest for 6th grade survey
#    97 records had no NAs for activities or 4-vector interest for 7th grade survey
#    70 records had no NAs for activities or 4-vector interest for 6th and 7th grade survey
attach(survey67);
II <- !is.na(Library_me_2) & !is.na(OMSI_me_2) & !is.na(Zoo_me_2) & !is.na(Child_museum_me_2) & !is.na(Scouts_me_2) & 
  !is.na(Library_me_7) & !is.na(OMSI_me_7) & !is.na(Zoo_me_7) & !is.na(Child_musuem_me_7) & !is.na(Scouts_me_7) &
  !is.na(Program_me_2) & !is.na(National_parks_me_2) & !is.na(Team_sport_me_2) & !is.na(Own_sport_me_2) &
  !is.na(Program_me_7) & !is.na(National_parks_me_7) & !is.na(Team_sport_me_7) & !is.na(Own_sport_me_7) &
  !is.na(Summer_camp_me_2) & !is.na(Hike_outdoors_me_2) & !is.na(Garden_me_2) & !is.na(Experiments_me_2) &
  !is.na(Summer_camp_me_7) & !is.na(Hike_outdoors_me_7) & !is.na(Garden_me_7) & !is.na(Experiments_me_7) &
  !is.na(Read_me_2) & !is.na(Internet_me_2) & !is.na(Computer_me_2) & !is.na(Communicate_me_2) & !is.na(TV_me_2) & 
  !is.na(Read_me_7) & !is.na(Internet_me_7) & !is.na(Computer_me_7) & !is.na(Communicate_me_7) & !is.na(TV_me_7) &
  !is.na(Build_me_2) & !is.na(Pets_me_2) &
  !is.na(Build_me_7) & !is.na(Pets_me_7) &
  !is.na(Earth_space_6) & !is.na(Human_bio_6) & !is.na(Tech_eng_6) & !is.na(Math_6) & 
  !is.na(Earth_space_7) & !is.na(Human_bio_7) & !is.na(Tech_eng_7) & !is.na(Math_7);
detach(survey67);
survey <- survey67[II,];

# Get only select columns to be pulled in by ABM
#   Survey_number, Sex, Elementary School Code, Science Teacher Code
#   Stuff I do
#   Who Encourages You
#   Stuff that interests me
#   Earth_space_6, Human_bio_6, Tech_eng_6, Math_6

# This allows me to reference the columns directly which helps with A's column names
# I had to change a couple of misspellings by hand.
# The Survey_number and Survey_number_2 are the same
# The list of activities is slightly different between 6th grade survey and 5th grade
# The Encouragement section also added a "Do Not Do" box.
attach(survey);
A <- data.frame(Survey_number, Sex, School_code_2, Science_teacher_2,
                Library_me_2, OMSI_me_2, Zoo_me_2, Child_museum_me_2,
                Scouts_me_2, Program_me_2, National_parks_me_2,
                Team_sport_me_2, Own_sport_me_2, Summer_camp_me_2,
                Hike_outdoors_me_2, Garden_me_2, Experiments_me_2, 
                Read_me_2, Internet_me_2, Computer_me_2, 
                Communicate_me_2, TV_me_2, Build_me_2, Pets_me_2,
                Library_parents_2,        Library_sibling_2,        Library_friends_2,        Library_no_one_2,        Library_do_not_2,
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
                Earth_space_6, Human_bio_6, Tech_eng_6, Math_6);
detach(survey);

# Normalize and rescale index values between 0 (No Interest) & 1 (Very Interested)
# Initially they scale from 1 to 5 with some NAs in there
# These are the last three columns 
A[,(ncol(A)-3):ncol(A)] <- (A[,(ncol(A)-3):ncol(A)] - 1 ) / 4;

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
# There are a couple of scattered NAs

# Scan the interest vector
for (i in 148:151)
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

# Now encode the Encouragement section
# Each youth will have a 4-digit code in this order
#  parents = 1 (not selected) or 2 (selected)
#  siblings = 1 (not selected) or 2 (selected)
#  friends = 1 (not selected) or 2 (selected)
#  no_one = 1 (not selected) or 2 (selected)
N = nrow(A);
Encourage = data.frame(Library_encourage=rep(0,N), OMSI_encourage=rep(0,N), Zoo_encourage=rep(0,N), Childrens_encourage=rep(0,N), 
                       Scouts_encourage=rep(0,N), Other_after_encourage=rep(0,N), National_park_encourage=rep(0,N), 
                       Team_sport_encourage=rep(0,N), Sport_encourage=rep(0,N), Summer_camps_encourage=rep(0,N), 
                       Hike_outdoors_encourage=rep(0,N), Garden_encourage=rep(0,N), Experiment_encourage=rep(0,N),
                       Read_encourage=rep(0,N), Internet_encourage=rep(0,N), Computer_games_encourage=rep(0,N), 
                       Communicate_encourage=rep(0,N), TV_encourage=rep(0,N), Build_encourage=rep(0,N), Pets_encourage=rep(0,N));
for (j in 1:dim(Encourage)[2])
{
  i = (j-1)*5 + 25;  # To get the column in A that I want. Each encouragement has 5 elements (parents, sibling, friend, no_one, do_not)
  Encourage[,j] = (A[,i]+1)*1000 + (A[,i+1]+1)*100 + (A[,i+2]+1)*10 + (A[,i+3]+1);  
}

# Now to combine the A dataframe (minus all the encouragement stuff) with Encourage
attach(A); 
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
                 Earth_space_6, Human_bio_6, Tech_eng_6, Math_6);
detach(A);
detach(Encourage);

# I want to duplicate some records to get close to or equal to original 144 in 6th grade
# but I won't replicate any more than double the records.
I <- sample.int(nrow(BB), size = min(144-nrow(BB), nrow(BB)));
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


#========================================================
#========================================================
#========================================================
# 7th Grade Data
# This allows me to reference the columns directly which helps with A's column names
# I had to change a couple of misspellings by hand.
# The Survey_number and Survey_number_2 are the same
# The list of activities is slightly different between 6th grade survey and 5th grade
# The Encouragement section also added a "Do Not Do" box.
N = nrow(survey);
attach(survey);
A7 <- data.frame(Survey_number, Sex, School_code_2, Science_teacher_7,
                Library_me_7, OMSI_me_7, Zoo_me_7, Child_musuem_me_7,
                Scouts_me_7, Program_me_7, National_parks_me_7,
                Team_sport_me_7, Own_sport_me_7, Summer_camp_me_7,
                Hike_outdoors_me_7, Garden_me_7, Experiments_me_7, 
                Read_me_7, Internet_me_7, Computer_me_7, 
                Communicate_me_7, TV_me_7, Build_me_7, Pets_me_7,
                Library_parents_7,        Library_sibling_7,        Library_friends_7,        Library_no_one_7,          Library_do_not_7,
                OMSI_parents_7=rep(NA,N),           OMSI_sibling_7=rep(NA,N),           OMSI_friends_7=rep(NA,N),           OMSI_no_one_7=rep(NA,N),           OMSI_do_not_7=rep(NA,N),
                Zoo_parents_7=rep(NA,N),            Zoo_sibling_7=rep(NA,N),            Zoo_friend_7=rep(NA,N),             Zoo_no_one_7=rep(NA,N),            Zoo_do_not_7=rep(NA,N),
                Childrens_parent_7=rep(NA,N),       Childrens_sibling_7=rep(NA,N),      Childrens_friend_7=rep(NA,N),       Childrens_no_one_7=rep(NA,N),      Childrens_do_not_7=rep(NA,N),
                Scouts_parents_7,         Scouts_sibling_7,         Scouts_friends_7,         Scouts_no_one_7,         Scouts_do_not_7,
                Other_after_parents_7=rep(NA,N),    Other_after_sibling_7=rep(NA,N),    Other_after_friend_7=rep(NA,N),     Other_after_no_one_7=rep(NA,N),    Other_after_do_not_7=rep(NA,N),
                National_park_parents_7=rep(NA,N),  National_park_sibling_7=rep(NA,N),  National_park_friends_7=rep(NA,N),  National_park_no_one_7=rep(NA,N),  National_park_do_not_7=rep(NA,N),
                Team_sport_parent_7,      Team_sport_sibling_7,     Team_sport_friend_7,	    Team_sport_no_one_7,	   Team_sport_do_not_7,
                Sport_parent_7=rep(NA,N),           Sport_sibling_7=rep(NA,N),	        Sport_friend_7=rep(NA,N),	          Sport_no_one_7=rep(NA,N),	         Sport_do_not_7=rep(NA,N),
                Summer_camps_parent_7=rep(NA,N),    Summer_camps_sibling_7=rep(NA,N),	  Summer_camps_friends_7=rep(NA,N),	  Summer_camps_no_one_7=rep(NA,N),	 Summer_camps_do_not_7=rep(NA,N),
                Hike_outdoors_parents_7,  Hike_outdoors_sibling_7,	Hike_outdoors_friends_7,	Hike_outdoors_no_one_7,	 Hike_outdoors_do_not_7,
                Garden_parents_7,         Garden_siblings_7,	      Garden_friends_7,	        Garden_no_one_7,	       Garden_do_not_7,
                Experiment_parents_7,     Experiment_siblings_7,	  Experiment_friends_7,	    Experiment_no_one_7,	   Experiment_do_not_7,
                Read_parents_7,           Read_siblings_7,	        Read_friends_7,	          Read_no_one_7,	         Read_do_not_7,
                Internet_parent_7,        Internet_sibling_7,	      Internet_friends_7,	      Internet_no_one_7,	     Internet_do_not_7,
                Computer_games_parent_7,  Computer_games_sibling_7,	Computer_games_friend_7,	Computer_games_no_one_7, Computer_games_do_not_7,
                Communicate_parent_7=rep(NA,N),     Communicate_sibling_7=rep(NA,N),	  Communicate_friend_7=rep(NA,N),	    Communicate_no_one_7=rep(NA,N),	   Communicate_do_not_7=rep(NA,N),
                TV_parents_7,             TV_siblings_7,	          TV_friends_7,	            TV_no_one_7,	           TV_do_not_7,
                Build_parents_7,          Build_siblings_7,	        Build_friends_7,	        Build_no_one_7,	         Build_do_not_7,
                Pets_parent_7,            Pets_sibling_7,	          Pets_friend_7,	          Pets_no_one_7,	         Pets_do_not_7,
                Stars_planets_7,  Mix_materials_7,	Weather_7,	Human_body_7,	
                Traits_7,	Sudoku_7,	Fish_hunt_7=rep(NA,N),	Earthquakes_7,	
                Measure_7,	Diseases_7,	Minecraft_7,	
                Planets_space_7,	Buildings_bridges_7,	Eat_exercise_7,	
                Engines_7,	Rocks_minerals_7=rep(NA,N),	Computers_cell_phones_7,	
                Food_flowers_7,	Maps_7,	Invent_7,	Community_green_7,	
                Shapes_7,	Puzzles_7,
                Earth_space_7, Human_bio_7, Tech_eng_7, Math_7);
detach(survey);

## WE NEED TO FILL IN THE COLUMNS THAT HAD NO DATA FOR 7th GRADE
## We will just copy the 6th grade data into the 7th grade.
## The 6th grade data use the _2 at the end, so I just replace the _7 with _2
missingCols <- c('OMSI_parents_7',          'OMSI_sibling_7',          'OMSI_friends_7',          'OMSI_no_one_7',          'OMSI_do_not_7',
                 'Zoo_parents_7',           'Zoo_sibling_7',           'Zoo_friend_7',            'Zoo_no_one_7',           'Zoo_do_not_7',
                 'Childrens_parent_7',      'Childrens_sibling_7',     'Childrens_friend_7',      'Childrens_no_one_7',     'Childrens_do_not_7',
                 'Other_after_parents_7',   'Other_after_sibling_7',   'Other_after_friend_7',    'Other_after_no_one_7',   'Other_after_do_not_7',
                 'National_park_parents_7', 'National_park_sibling_7', 'National_park_friends_7', 'National_park_no_one_7', 'National_park_do_not_7',
                 'Sport_parent_7',          'Sport_sibling_7',         'Sport_friend_7',          'Sport_no_one_7',         'Sport_do_not_7',
                 'Summer_camps_parent_7',   'Summer_camps_sibling_7',  'Summer_camps_friends_7',  'Summer_camps_no_one_7',  'Summer_camps_do_not_7',
                 'Communicate_parent_7',    'Communicate_sibling_7',   'Communicate_friend_7',    'Communicate_no_one_7',   'Communicate_do_not_7',
                 'Fish_hunt_7', 'Rocks_minerals_7')
for (col in missingCols)
{
  oldCol <- sub('7$','2',col);
  A7[[col]] = A[[oldCol]];
}


# Normalize and rescale index values between 0 (No Interest) & 1 (Very Interested)
# Initially they scale from 1 to 5 with some NAs in there
# These are the last three columns 
A7[,(ncol(A7)-3):ncol(A7)] <- (A7[,(ncol(A7)-3):ncol(A7)] - 1 ) / 4;

####### DATA CLEANING #########
# Quick scan to see that all activity values are within valid range of 1-5.
for (i in 5:24)
{
  xmin = min(A7[,i], na.rm=TRUE);
  xmax = max(A7[,i], na.rm=TRUE);
  if (xmin < 1)
  {
    print(c(names(A7)[i],'is below minimun at index'));
    indexValue <- match(xmin,A7[,i]);
    print(c(indexValue, xmin));    
  }
  if (xmax > 5)
  {
    print(c(names(A7)[i],'is above maximum at index'));
    indexValue <- match(xmax,A7[,i]);
    #    badRecord <- A7[,i] == max(A7[,i],na.rm=TRUE) & !is.na(A7[,i]);
    print(c(indexValue,xmax));
  }
}

# Quick scan to see that all encouragement is either 0 or 1
# Let's set any NA as a 0.
for (i in 25:124)
{
  xmin = min(A7[,i], na.rm=TRUE);
  xmax = max(A7[,i], na.rm=TRUE);
  if (xmin < 0)
  {
    print(c(names(A7)[i],'is below minimun at index'));
    indexValue <- match(xmin,A7[,i]);
    print(c(indexValue, xmin));    
  }
  if (xmax > 1)
  {
    print(c(names(A7)[i],'is above maximum at index'));
    indexValue <- match(xmax,A7[,i]);
    #    badRecord <- A7[,i] == max(A7[,i],na.rm=TRUE) & !is.na(A7[,i]);
    print(c(indexValue,xmax));
  }  
  badRecord <- is.na(A7[,i]);
  A7[badRecord,i] = 0;
}

# Quick scan to see that all Interests are in valid scan
for (i in 125:147)
{
  xmin = min(A7[,i], na.rm=TRUE);
  xmax = max(A7[,i], na.rm=TRUE);
  if (xmin < 1)
  {
    print(c(names(A7)[i],'is below minimun at index'));
    indexValue <- match(xmin,A7[,i]);
    print(c(indexValue, xmin));    
  }
  if (xmax > 5)
  {
    print(c(names(A7)[i],'is above maximum at index'));
    indexValue <- match(xmax,A7[,i]);
    print(c(indexValue,xmax));
  } 
  indexNA <- match(NA,A7[,i]);
  print(c(names(A7)[i],'has NA at index values'));
  print(indexNA);
}
# Some sporadic records have missing data for interests

# Scan the interest vector
for (i in 148:151)
{
  xmin = min(A7[,i],na.rm=TRUE);
  xmax = max(A7[,i],na.rm=TRUE);
  if (xmin < 0)
  {
    print(c(names(A7)[i], 'is below minimum at index'));
    indexValue <- match(xmin,A7[,i]);
    print(c(indexValue,xmin));
  }
  
  if (xmax > 1)
  {
    print(c(names(A7)[i], 'is below minimum at index'));
    indexValue <- match(xmin,A7[,i]);
    print(c(indexValue,xmin));
  }  
  indexNA <- match(NA,A7[,i]);
  print(c(names(A7)[i],'has NA at index values'));
  print(indexNA);
}

####### END DATA CLEANING #########


# Now encode the Encouragement section
# Each youth will have a 4-digit code in this order
#  parents = 1 (not selected) or 2 (selected)
#  siblings = 1 (not selected) or 2 (selected)
#  friends = 1 (not selected) or 2 (selected)
#  no_one = 1 (not selected) or 2 (selected)
N = nrow(A7);
Encourage = data.frame(Library_encourage=rep(0,N), OMSI_encourage=rep(0,N), Zoo_encourage=rep(0,N), Childrens_encourage=rep(0,N), 
                       Scouts_encourage=rep(0,N), Other_after_encourage=rep(0,N), National_park_encourage=rep(0,N), 
                       Team_sport_encourage=rep(0,N), Sport_encourage=rep(0,N), Summer_camps_encourage=rep(0,N), 
                       Hike_outdoors_encourage=rep(0,N), Garden_encourage=rep(0,N), Experiment_encourage=rep(0,N),
                       Read_encourage=rep(0,N), Internet_encourage=rep(0,N), Computer_games_encourage=rep(0,N), 
                       Communicate_encourage=rep(0,N), TV_encourage=rep(0,N), Build_encourage=rep(0,N), Pets_encourage=rep(0,N));
for (j in 1:dim(Encourage)[2])
{
  i = (j-1)*5 + 25;  # To get the column in A7 that I want. Each encouragement has 5 elements (parents, sibling, friend, no_one, do_not)
  Encourage[,j] = (A7[,i]+1)*1000 + (A7[,i+1]+1)*100 + (A7[,i+2]+1)*10 + (A7[,i+3]+1);  
}

# Now to combine the A7 dataframe (minus all the encouragement stuff) with Encourage
attach(A7); 
attach(Encourage);
BB7 <- data.frame(Survey_number, Sex, School_code_2, Science_teacher_7,
                 Library_me_7, OMSI_me_7, Zoo_me_7, Child_musuem_me_7,
                 Scouts_me_7, Program_me_7, National_parks_me_7,
                 Team_sport_me_7, Own_sport_me_7, Summer_camp_me_7,
                 Hike_outdoors_me_7, Garden_me_7, Experiments_me_7, 
                 Read_me_7, Internet_me_7, Computer_me_7, 
                 Communicate_me_7, TV_me_7, Build_me_7, Pets_me_7,
                 Library_encourage, OMSI_encourage, Zoo_encourage, Childrens_encourage, Scouts_encourage,
                 Other_after_encourage, National_park_encourage, Team_sport_encourage, Sport_encourage,
                 Summer_camps_encourage, Hike_outdoors_encourage, Garden_encourage, Experiment_encourage,
                 Read_encourage, Internet_encourage, Computer_games_encourage, Communicate_encourage,
                 TV_encourage, Build_encourage, Pets_encourage,
                 Stars_planets_7,  Mix_materials_7,	Weather_7,	Human_body_7,	
                 Traits_7,	Sudoku_7,	Fish_hunt_7,	Earthquakes_7,	
                 Measure_7,	Diseases_7,	Minecraft_7,	
                 Planets_space_7,	Buildings_bridges_7,	Eat_exercise_7,	
                 Engines_7,	Rocks_minerals_7,	Computers_cell_phones_7,	
                 Food_flowers_7,	Maps_7,	Invent_7,	Community_green_7,	
                 Shapes_7,	Puzzles_7,
                 Earth_space_7, Human_bio_7, Tech_eng_7, Math_7);
detach(A7);
detach(Encourage);

# Use the same duplicates as before for the 6th grade.
C7 <- data.frame(BB7[I,]);  # Duplicated records
# Change the survey number so that it stays unique.  I'm going to multiply by 1000;
C7$Survey_number <- C7$Survey_number*1000;

# Final data for outputing to csv file for ABM.
# Take original data BB and append resampled records in C.
D7 <- rbind(BB7,C7);

filename <- "seventhGradeStudentInput.csv"
write.table(D7, filename, quote=FALSE, sep=',', row.names=FALSE);

# TODO:  Rewrite so I can just use the filename variable.
#Remove blanks at end of string columns.  Need to escape the \, so use \\
system("perl -p -i -e 's/\\s+,/,/g' seventhGradeStudentInput.csv");
#Remove blanks at end of line
system("perl -p -i -e 's/\\s+$/\\n/g' seventhGradeStudentInput.csv");
#Add NA to end of line if missing data
system("perl -p -i -e 's/,$/,NA/g' seventhGradeStudentInput.csv");
#Insert NA into blank fields
system("perl -p -i -e 's/,,/,NA,/g' seventhGradeStudentInput.csv");
#Needs to be run at least twice if ,,,
system("perl -p -i -e 's/,,/,NA,/g' seventhGradeStudentInput.csv");
system("perl -p -i -e 's/,,/,NA,/g' seventhGradeStudentInput.csv");
system("perl -p -i -e 's/,,/,NA,/g' seventhGradeStudentInput.csv");


