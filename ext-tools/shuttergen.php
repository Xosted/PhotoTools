<?php

$shutters = array("512 min", "406 min", "323 min",
            "256 min", "203 min", "161 min",
            "128 min", "102 min", "80.6 min",
            "64 min", "50.8 min", "40.3 min",
            "32 min", "25.4 min", "20.2 min",
            "16 min", "12.7 min", "10.1 min",
            "8 min", "6.3 min", "5 min",
            "4 min", "3.2 min", "2.5 min",
            "2 min","1.6 min", "1.3 min",
            "1 min", "50 sec", "40 sec",
            "30 sec", "25 sec", "20 sec",
            "15 sec", "13 sec", "10 sec",
            "8 sec", "6 sec", "5 sec",
            "4 sec", "3 sec", "2.5 sec",
            "2 sec", "1.6 sec", "1.3 sec",
            "1 sec", "1/1.3 sec", "1/1.6 sec",
            "1/2 sec", "1/2.5 sec", "1/3 sec",
            "1/4 sec", "1/5 sec", "1/6 sec",
            "1/8 sec", "1/10 sec", "1/13 sec",
            "1/15 sec", "1/20 sec", "1/25 sec",
            "1/30 sec", "1/40 sec", "1/50 sec",
            "1/60 sec", "1/80 sec", "1/100 sec",
            "1/125 sec", "1/160 sec", "1/200 sec",
            "1/250 sec", "1/320 sec", "1/400 sec",
            "1/500 sec", "1/640 sec", "1/800 sec",
            "1/1000 sec", "1/1250 sec", "1/1600 sec",
            "1/2000 sec", "1/2500 sec", "1/3200 sec",
            "1/4000 sec", "1/5000 sec", "1/6400 sec",
            "1/8000 sec"  );

$aperture = array("1", "1.12", "1.25",
            "1.4", "1.6", "1.8",
            "2.0", "2.3", "2.5",
            "2.8", "3.2", "3.6",
            "4", "4.5", "5",
            "5.6", "6.3", "7",
            "8", "9", "10",
            "11", "12.5", "14",
            "16", "18", "20",
            "22", "25", "28",
            "32", "36", "40",
            "45", "50", "57",
            "64", "72", "80",
            "90", "100", "115",
            "128", "145", "160",
            "180", "200", "230",
            "256", "290", "320",
            "360" );

$count = count($aperture);
$shutterCount = count($shutters);

for ($i = 0; $i < $shutterCount + $count - 1; $i++) {

    echo "{";

    $z = $i;
    
    for ($x = 0; $x < $count; $x++) {
	if ($z > -1 && $z < $shutterCount) echo '"' . $shutters[$z] . '"';
	else echo '"0"';
	$z--;
	if ($x < ($count - 1)) echo ",";
    }

    echo "},\n";
}

?>
