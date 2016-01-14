# Reading in the survey data provided by SYNERGIES
# May_2015_-8th Grade/Survey_May_2015_9.sav provided by Synergies.
# Starting with the 6th grade data for model initializaiton
# Using the 4-component interest vector
#
# 5th Grade         6th Grade       7th Grade
#  Earth_space_5    Earth_space_6    Earth_space_7
#  Human_bio_5      Human_bio_6      Human_bio_7
#  Tech_eng_5       Tech_eng_6       Tech_eng_7
#                   Math_6           Math_7
# I access SPSS 23 and did a "Save As" csv file using both the 
# "Using data values" & "Using label values" results.
# For the most part I want to use the data values file.
# I use my removeCtrlM.pl program to remove the ^M from csv files.
setwd("/Users/mhendrey/Dropbox/Synergies/Surveys/May_2015-8th Grade/");
surveyAll <- read.csv("survey_may_2015_abm_datavaluesRemoveCtrlM.dat", sep='\t', stringsAsFactors=FALSE);

# To get only the 5th grade surveys, Survey_5 == 1 (174 of these)
# If I wanted the 6th grade surveys, Survey_6 == 1 (145 of these)
# If I wanted the 7th grade surveys, Survey_7 == 1 (162 of these)
# If I wanted the 8th grade surveys, Survey_8 == 1 (154 of these)
survey8 <- surveyAll[surveyAll$Survey_8 & !is.na(surveyAll$Survey_8),];
# 6th and 7th Grade (120 records)
#survey67 <- surveyAll[surveyAll$Survey_6 & !is.na(surveyAll$Survey_6) & 
#                      surveyAll$Survey_7 & !is.na(surveyAll$Survey_7),];
# 6th, 7th, and 8th Grade (90 records)
#survey678<- surveyAll[surveyAll$Survey_6 & !is.na(surveyAll$Survey_6) & 
#                      surveyAll$Survey_7 & !is.na(surveyAll$Survey_7) &
#                      surveyAll$Survey_8 & !is.na(surveyAll$Survey_8),]

# Keep only those records where the activities and 4-vector interest don't have any NA.
# Out of the 114 records that filled in botht the 6th & 7th grade survey
#    77 records had no NAs for activities or 4-vector interest for 6th grade survey
#    97 records had no NAs for activities or 4-vector interest for 7th grade survey
#    70 records had no NAs for activities or 4-vector interest for 6th and 7th grade survey
attach(survey8);
II <- !is.na(Library_me_8) & !is.na(OMSI_me_8) & !is.na(Zoo_me_8) & !is.na(Child_museum_me_8) & !is.na(Scouts_me_8) &
  !is.na(Program_me_8) & !is.na(National_parks_me_8) & !is.na(Team_sport_me_8) & !is.na(Own_sport_me_8) &
  !is.na(Summer_camp_me_8) & !is.na(Hike_outdoors_me_8) & !is.na(Garden_me_8) & !is.na(Experiments_me_8) &
  !is.na(Read_me_8) & !is.na(Internet_me_8) & !is.na(Computer_me_8) & !is.na(Communicate_me_8) & !is.na(TV_me_8) &
  !is.na(Build_me_8) & !is.na(Pets_me_8) &
  !is.na(Earth_space_8) & !is.na(Human_bio_8) & !is.na(Tech_eng_8) & !is.na(Math_8);
detach(survey8);
survey <- survey8[II,];
# If I used the survey678 and the following logic, I get only 52 records.
#II <- !is.na(Library_me_2) & !is.na(OMSI_me_2) & !is.na(Zoo_me_2) & !is.na(Child_museum_me_2) & !is.na(Scouts_me_2) & 
#  !is.na(Program_me_2) & !is.na(National_parks_me_2) & !is.na(Team_sport_me_2) & !is.na(Own_sport_me_2) &
#  !is.na(Summer_camp_me_2) & !is.na(Hike_outdoors_me_2) & !is.na(Garden_me_2) & !is.na(Experiments_me_2) &
#  !is.na(Read_me_2) & !is.na(Internet_me_2) & !is.na(Computer_me_2) & !is.na(Communicate_me_2) & !is.na(TV_me_2) & 
#  !is.na(Build_me_2) & !is.na(Pets_me_2) &
#  !is.na(Build_me_7) & !is.na(Pets_me_7) &
#  !is.na(Earth_space_6) & !is.na(Human_bio_6) & !is.na(Tech_eng_6) & !is.na(Math_6_copy) & 
#  !is.na(Earth_space_7) & !is.na(Human_bio_7) & !is.na(Tech_eng_7) & !is.na(Math_7) &
#  !is.na(Library_me_8) & !is.na(OMSI_me_8) & !is.na(Zoo_me_8) & !is.na(Child_museum_me_8) & !is.na(Scouts_me_8) &
#  !is.na(Program_me_8) & !is.na(National_parks_me_8) & !is.na(Team_sport_me_8) & !is.na(Own_sport_me_8) &
#  !is.na(Summer_camp_me_8) & !is.na(Hike_outdoors_me_8) & !is.na(Garden_me_8) & !is.na(Experiments_me_8) &
#  !is.na(Read_me_8) & !is.na(Internet_me_8) & !is.na(Computer_me_8) & !is.na(Communicate_me_8) & !is.na(TV_me_8) &
#  !is.na(Build_me_8) & !is.na(Pets_me_8) &
#  !is.na(Earth_space_8) & !is.na(Human_bio_8) & !is.na(Tech_eng_8) & !is.na(Math_8);

# Get only select columns to be pulled in by ABM
#   Survey_number, Sex, 6th Grade School Code, Science Teacher Code
#   Stuff I do
#   Who Encourages You
#   Stuff that interests me
#   Earth_space_6, Human_bio_6, Tech_eng_6, Math_6

# This allows me to reference the columns directly which helps with A's column names
# I had to change a couple of misspellings by hand.
# The Survey_number and Survey_number_2 are the same
# The list of activities is slightly different between 6th grade survey and 5th grade
# The Encouragement section also added a "Do Not Do" box.
N = nrow(survey)
attach(survey);
A <- data.frame(Survey_number, Sex, School_code_2, Science_teacher_8,
                Library_me_8, OMSI_me_8, Zoo_me_8, Child_museum_me_8,
                Scouts_me_8, Program_me_8, National_parks_me_8,
                Team_sport_me_8, Own_sport_me_8, Summer_camp_me_8,
                Hike_outdoors_me_8, Garden_me_8, Experiments_me_8, 
                Read_me_8, Internet_me_8, Computer_me_8, 
                Communicate_me_8, TV_me_8, Build_me_8, Pets_me_8,
                Library_parents_8,        Library_sibling_8,        Library_friends_8,        Library_no_one_8,        Library_do_not_8,
                OMSI_parents_8=rep(NA,N),           OMSI_sibling_8=rep(NA,N),           OMSI_friends_8=rep(NA,N),           OMSI_no_one_8=rep(NA,N),           OMSI_do_not_8=rep(NA,N),
                Zoo_parents_8=rep(NA,N),            Zoo_sibling_8=rep(NA,N),            Zoo_friend_8=rep(NA,N),             Zoo_no_one_8=rep(NA,N),            Zoo_do_not_8=rep(NA,N),
                Childrens_parent_8=rep(NA,N),       Childrens_sibling_8=rep(NA,N),      Childrens_friend_8=rep(NA,N),       Childrens_no_one_8=rep(NA,N),      Childrens_do_not_8=rep(NA,N),
                Scouts_parents_8,         Scouts_sibling_8,         Scouts_friends_8,         Scouts_no_one_8,         Scouts_do_not_8,
                Other_after_parents_8=rep(NA,N),    Other_after_sibling_8=rep(NA,N),    Other_after_friend_8=rep(NA,N),     Other_after_no_one_8=rep(NA,N),    Other_after_do_not_8=rep(NA,N),
                National_park_parents_8=rep(NA,N),  National_park_sibling_8=rep(NA,N),  National_park_friends_8=rep(NA,N),  National_park_no_one_8=rep(NA,N),  National_park_do_not_8=rep(NA,N),
                Team_sport_parents_8,      Team_sport_sibling_8,     Team_sport_friends_8,      Team_sport_no_one_8,	   Team_sport_do_not_8,
                Sport_parent_8=rep(NA,N),           Sport_sibling_8=rep(NA,N),	        Sport_friend_8=rep(NA,N),	          Sport_no_one_8=rep(NA,N),	         Sport_do_not_8=rep(NA,N),
                Summer_camps_parent_8=rep(NA,N),    Summer_camps_sibling_8=rep(NA,N),	  Summer_camps_friends_8=rep(NA,N),	  Summer_camps_no_one_8=rep(NA,N),	 Summer_camps_do_not_8=rep(NA,N),
                Hike_outdoors_parents_8,  Hike_outdoors_sibling_8,	Hike_outdoors_friends_8,	Hike_outdoors_no_one_8,	 Hike_outdoors_do_not_8,
                Garden_parents_8,         Garden_sibling_8,	      Garden_friends_8,	        Garden_no_one_8,	       Garden_do_not_8,
                Experiment_parents_8,     Experiment_sibling_8,	  Experiment_friends_8,	    Experiment_no_one_8,	   Experiment_do_not_8,
                Read_parents_8,           Read_sibling_8,	        Read_friends_8,	          Read_no_one_8,	         Read_do_not_8,
                Internet_parents_8,        Internet_sibling_8,	      Internet_friends_8,	      Internet_no_one_8,	     Internet_do_not_8,
                Computer_games_parents_8,  Computer_games_sibling_8,	Computer_games_friends_8,	Computer_games_no_one_8, Computer_games_do_not_8,
                Communicate_parent_8=rep(NA,N),     Communicate_sibling_8=rep(NA,N),	  Communicate_friend_8=rep(NA,N),	    Communicate_no_one_8=rep(NA,N),	   Communicate_do_not_8=rep(NA,N),
                TV_parents_8,             TV_sibling_8,	          TV_friends_8,	            TV_no_one_8,	           TV_do_not_8,
                Build_parents_8,          Build_sibling_8,	        Build_friends_8,	        Build_no_one_8,	         Build_do_not_8,
                Pets_parents_8,            Pets_sibling_8,	          Pets_friends_8,	          Pets_no_one_8,	         Pets_do_not_8,
                Stars_planets_8,  Mix_materials_8,	Weather_8,	Human_body_8,	
                Traits_passed_8,	Sudoku_math_8,	Fish_hunt_8=rep(NA,N),	Earthquakes_volcanoes_8,	
                Measure_size_8,	Diseases_8,	Play_Minecraft_8,	
                Exploring_space_8,	Buildings_bridges_8,	Eat_exercise_8,	
                Gas_diesel_8,	Rocks_minerals_8=rep(NA,N),	Comuters_cell_phones_8,	
                Grow_food_8,	Use_maps_8,	Design_games_8,	Community_green_8,	
                Make_shapes_8,	Solve_puzzles_8,
                Earth_space_8, Human_bio_8, Tech_eng_8, Math_8);
detach(survey);


## WE NEED TO FILL IN THE COLUMNS THAT HAD NO DATA FOR 7th GRADE
## We will just copy the 6th grade data into the 7th grade.
## The 6th grade data use the _2 at the end, so I just replace the _7 with _2
#missingCols <- c('OMSI_parents_7',          'OMSI_sibling_7',          'OMSI_friends_7',          'OMSI_no_one_7',          'OMSI_do_not_7',
#                 'Zoo_parents_7',           'Zoo_sibling_7',           'Zoo_friend_7',            'Zoo_no_one_7',           'Zoo_do_not_7',
#                 'Childrens_parent_7',      'Childrens_sibling_7',     'Childrens_friend_7',      'Childrens_no_one_7',     'Childrens_do_not_7',
#                 'Other_after_parents_7',   'Other_after_sibling_7',   'Other_after_friend_7',    'Other_after_no_one_7',   'Other_after_do_not_7',
#                 'National_park_parents_7', 'National_park_sibling_7', 'National_park_friends_7', 'National_park_no_one_7', 'National_park_do_not_7',
#                 'Sport_parent_7',          'Sport_sibling_7',         'Sport_friend_7',          'Sport_no_one_7',         'Sport_do_not_7',
#                 'Summer_camps_parent_7',   'Summer_camps_sibling_7',  'Summer_camps_friends_7',  'Summer_camps_no_one_7',  'Summer_camps_do_not_7',
#                 'Communicate_parent_7',    'Communicate_sibling_7',   'Communicate_friend_7',    'Communicate_no_one_7',   'Communicate_do_not_7',
#                 'Fish_hunt_7', 'Rocks_minerals_7')
#for (col in missingCols)
#{
#  oldCol <- sub('7$','2',col);
#  A7[[col]] = A[[oldCol]];
#}


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
    #    badRecord <- A7[,i] == max(A7[,i],na.rm=TRUE) & !is.na(A7[,i]);
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
    indexValue <- match(xmin,A7[,i]);
    print(c(indexValue, xmin));    
  }
  if (xmax > 1)
  {
    print(c(names(A)[i],'is above maximum at index'));
    indexValue <- match(xmax,A[,i]);
    #    badRecord <- A7[,i] == max(A7[,i],na.rm=TRUE) & !is.na(A7[,i]);
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
# Some sporadic records have missing data for interests

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

# Normalize and rescale index values between 0 (No Interest) & 1 (Very Interested)
# Initially they scale from 1 to 5 with some NAs in there
# These are the last three columns 
A[,(ncol(A)-3):ncol(A)] <- (A[,(ncol(A)-3):ncol(A)] - 1 ) / 4;

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
  i = (j-1)*5 + 25;  # To get the column in A7 that I want. Each encouragement has 5 elements (parents, sibling, friend, no_one, do_not)
  Encourage[,j] = (A[,i]+1)*1000 + (A[,i+1]+1)*100 + (A[,i+2]+1)*10 + (A[,i+3]+1);  
}

# Now to combine the A dataframe (minus all the encouragement stuff) with Encourage
attach(A); 
attach(Encourage);
BB <- data.frame(Survey_number, Sex, School_code_2, Science_teacher_8,
                 Library_me_8, OMSI_me_8, Zoo_me_8, Child_museum_me_8,
                 Scouts_me_8, Program_me_8, National_parks_me_8,
                 Team_sport_me_8, Own_sport_me_8, Summer_camp_me_8,
                 Hike_outdoors_me_8, Garden_me_8, Experiments_me_8, 
                 Read_me_8, Internet_me_8, Computer_me_8, 
                 Communicate_me_8, TV_me_8, Build_me_8, Pets_me_8,
                 Library_encourage, OMSI_encourage, Zoo_encourage, Childrens_encourage, Scouts_encourage,
                 Other_after_encourage, National_park_encourage, Team_sport_encourage, Sport_encourage,
                 Summer_camps_encourage, Hike_outdoors_encourage, Garden_encourage, Experiment_encourage,
                 Read_encourage, Internet_encourage, Computer_games_encourage, Communicate_encourage,
                 TV_encourage, Build_encourage, Pets_encourage,
                 Stars_planets_8,  Mix_materials_8,  Weather_8,	Human_body_8,	
                 Traits_passed_8,	Sudoku_math_8,	Fish_hunt_8,	Earthquakes_volcanoes_8,	
                 Measure_size_8,	Diseases_8,	Play_Minecraft_8,	
                 Exploring_space_8,	Buildings_bridges_8,	Eat_exercise_8,	
                 Gas_diesel_8,	Rocks_minerals_8,	Comuters_cell_phones_8,	
                 Grow_food_8,	Use_maps_8,	Design_games_8,	Community_green_8,	
                 Make_shapes_8,	Solve_puzzles_8,
                 Earth_space_8, Human_bio_8, Tech_eng_8, Math_8);
detach(A);
detach(Encourage);

# Use the same duplicates as before for the 6th grade.
#C7 <- data.frame(BB7[I,]);  # Duplicated records
# Change the survey number so that it stays unique.  I'm going to multiply by 1000;
#C7$Survey_number <- C7$Survey_number*1000;
BB$Survey_number <- BB$Survey_number*1000000;

# Final data for outputing to csv file for ABM.
# Take original data BB and append resampled records in C.
#D7 <- rbind(BB7,C7);

filename <- "eighthGradeStudentInput.csv"
write.table(BB, filename, quote=FALSE, sep=',', row.names=FALSE);

# TODO:  Rewrite so I can just use the filename variable.
#Remove blanks at end of string columns.  Need to escape the \, so use \\
system("perl -p -i -e 's/\\s+,/,/g' eighthGradeStudentInput.csv");
#Remove blanks at end of line
system("perl -p -i -e 's/\\s+$/\\n/g' eighthGradeStudentInput.csv");
#Add NA to end of line if missing data
system("perl -p -i -e 's/,$/,NA/g' eighthGradeStudentInput.csv");
#Insert NA into blank fields
system("perl -p -i -e 's/,,/,NA,/g' eighthGradeStudentInput.csv");
#Needs to be run at least twice if ,,,
system("perl -p -i -e 's/,,/,NA,/g' eighthGradeStudentInput.csv");
system("perl -p -i -e 's/,,/,NA,/g' eighthGradeStudentInput.csv");
system("perl -p -i -e 's/,,/,NA,/g' eighthGradeStudentInput.csv");


