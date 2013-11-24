# Reading in the survey data provided by SYNERGIES
# Nov_2013-6th Grade/Merge_final_May_2013.sav provided by Synergies.
# This redoes the 5th grade PCA indices using the weights from 6th grade analysis.
# Indices are now the following
#    5th Grade          6th Grade
#   Earth_space         Earth_space_6
#   Human_bio           Human_bio_6
#   Tech_eng            Tech_eng_6

# I access SPSS 21 and did a "Save As" csv file using both the 
# "Using data values" & "Using label values" results.
# For the most part I want to use the data values file.
# I use my removeCtrlM.pl program to remove the ^M from csv files.
setwd("/Users/mhendrey/Documents/Eclipse/workspace/StemStudents/data/");
surveyAll <- read.csv("original/merge_final_may_2013_datavaluesRemoveCtrlM.csv", stringsAsFactors=FALSE);

# To grab only the 5th grade surveys, ID == 1
# If I wanted the 6th grade surveys, ID_2 == 3
survey5 <- surveyAll[surveyAll$ID == 1 & !is.na(surveyAll$ID),];

# Get only select columns to be pulled in by ABM
#   Survey_number, Sex, Elementary School Code, Science Teacher Code
#   Stuff I do
#   Stuff that interests me
#   Earth/Space Index, Human/Bio Index, Tech/Eng Index

# This allows me to reference the columns directly which helps with A's column names
attach(survey5);
A <- data.frame(Survey_number, Sex, School_code, Teacher_code,
                Library_me, Museum_me, Scouts_me, National_Parks_me,
                Afterschool_me, Talk_me, Summer_camp_me,
                Hike_outdoors_me, Garden_me, Experiments_me, Read_me,
                Internet_me, Computer_me, TV_me, Build_me,
                Stars_planets, mix_materials, Weather, Human_body,
                Traits, Dinosaurs, Fish_hunt, Earthquakes,
                Instruments, Diseases, Dangerous_plants_animals,
                Planets_space, Buildings_bridges, Eat_exercise,
                Engines, Rocks_minerals, Computers_cell_phones,
                Puzzels_math, Food_flowers, Maps, Invent,
                Community_green, Pets,
                Earth_space, Human_bio, Tech_eng);
detach(survey5);

# Normalize and rescale index values between 0 (No Interest) & 1 (Very Interested)
# These are the last three columns 
A[,(ncol(A)-2):ncol(A)] <- (A[,(ncol(A)-2):ncol(A)] - 1 ) / 4;

# Quick scan to see that all values are within a valid range of 1-5.

for (i in 5:ncol(A))
{
  print(names(A)[i]);
  print(summary(A[,i]));
}

# Museum_me has max value of 54.  Let's set it to 4
badRecord <- A$Museum_me == max(A$Museum_me,na.rm=TRUE) & !is.na(A$Museum_me);
A$Museum_me[badRecord] = 4;

# Keep only records that have no NA's in columns 5:19 (Stuff I Do) & 43:45 (Interest Vector).
attach(A);
JJ <- !is.na(Library_me) & !is.na(Museum_me) & !is.na(Scouts_me) & !is.na(National_Parks_me) & !is.na(Afterschool_me) &
  !is.na(Talk_me) & !is.na(Summer_camp_me) & !is.na(Hike_outdoors_me) & !is.na(Garden_me) & !is.na(Experiments_me) &
  !is.na(Read_me) & !is.na(Internet_me) & !is.na(Computer_me) & !is.na(TV_me) & !is.na(Build_me) &
  !is.na(Earth_space) & !is.na(Human_bio) & !is.na(Tech_eng);
detach(A);

# This leaves me with 129 records out of 174
B <- A[JJ,];

# Now I want to duplicate some of the records to get back to 174
I <- sample.int(nrow(B), size = nrow(A)-nrow(B));
C <- data.frame(B[I,]);  # Duplicated records
# Change the survey number so that it stays unique.  I'm going to multiply by 1000;
C$Survey_number <- C$Survey_number*1000;

# Final data for outputing to csv file for ABM.
# Take original data B and append resampled records in C.
D <- rbind(B,C);

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
