# CS-200-WaTor
A simulation of shark and fish populations in an environment based on http://home.cc.gatech.edu/biocs1/uploads/2/wator_dewdney.pdf

This can read user input of a filename that you choose, which it would then find and parse through to load simulation parameters into the program. If you enter any parameters it'll ask you if you want to save them somewhere too. After the simulation ends it asks if you'd like to save the population chart to a file that you choose.

The parameters are:
"seed": an integer which is used with the random generator that decides where the fish are moving to, 
"ocean_width", 
"ocean_height", 
"starting_fish", 
"starting_sharks",  
"fish_breed": at what age do fish reproduce at, 
"sharks_breed": at what age do sharks reproduce at, 
"sharks_starve": at what age do sharks die from hunger, the fish are assumed to never die unless eaten by a shark.
