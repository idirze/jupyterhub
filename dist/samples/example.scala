
/*
Written by Trevor Grant 10/22/2015
 Inspired by word count example at: http://spark.apache.org/examples.html
*/ 

val text = sc.parallelize(List("In the time of chimpanzees, I was a monkey",  // some lines of text to analyze
"Butane in my veins and I'm out to cut the junkie",
"With the plastic eyeballs, spray paint the vegetables",
"Dog food stalls with the beefcake pantyhose",
"Kill the headlights and put it in neutral",
"Stock car flamin' with a loser in the cruise control",
"Baby's in Reno with the Vitamin D",
"Got a couple of couches, sleep on the love seat",
"Someone came in sayin' I'm insane to complain",
"About a shotgun wedding and a stain on my shirt",
"Don't believe everything that you breathe",
"You get a parking violation and a maggot on your sleeve",
"So shave your face with some mace in the dark",
"Savin' all your food stamps and burnin' down the trailer park",
"Yo, cut it"))


/*  The meat and potatoes:
        this tells flink to iterate through the elements, in this case strings,
        transform the string to lower case and split the string at white space into individual words
        then finally aggregate the occurance of each word. 
        
        This creates the count variable which is a list of tuples of the form (word, occurances)
*/
val counts = text.flatMap { _.toLowerCase.split("\\W+") }
                 .map { (_,1) }
                 .reduceByKey(_ + _)

counts.collect().foreach(println(_))  // execute the script and print each element in the counts list