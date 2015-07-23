#!/usr/bin/perl -w

#################################################################################
#                                                                               #
# Program to submit bootstrap-ARACNE jobs to cluster running the Sun Grid       #
#   Engine job scheduler                                                        #
#                                                                               #
# Usage: qsubbootstrap.pl <output dir> <lower#> <upper#> <ARACNE arguments>     #
# Example: qsubbootstrap.pl adjdir 1 100 -i data.exp -l tflist.txt -s probe.txt #
#          -p 1e-5 -e 0                                                         #
#                                                                               #
# Description: This program submit the bootstrap-ARACNE jobs to cluster and     #
#   store all the output bootstrapping networks in the directory <output dir>.  #
#   The directory will be created if it doesn't exist.                          #
#   Users also need to specify the range of bootstrapping samples to submit.    #
#   Please make sure you have ARACNE executable & configuration files in your   #
#   current working directory. Upon finish running the jobs, use script         #
#   getconsensusnet.pl to construct the consensus network.                      #
#                                                                               #
#################################################################################

use Fcntl;
if (scalar(@ARGV) < 4) {
  die "Incorrect number of arguments!\nUsage: qsubbootstrap.pl <output dir> <lower#> <upper#> <ARACNE arguments> \nExample: qsubbootstrap.pl adjdir 1 100 -i data.exp -l tflist.txt -p 1e-5 -e 0\n";
} else {
  ($dir,$r1,$r2,@otherarg)=@ARGV;
  $otherarg="@otherarg";
} 

$otherarg =~ m/-i\s+(\S+)\.\S+\s/;
$inputfile = $1;
$dir=$dir."\/" unless ($dir=~m/\/$/);
@folderfound=<./*/>;
$allfolder="@folderfound";
system "mkdir ".$dir unless ($allfolder=~m/$dir/);

foreach $bs ($r1..$r2){
  $bs3 = sprintf("%03i", $bs);
  $shfile = "aracne".$bs3."\.sh";
  sysopen(OUTFILE, $shfile, O_WRONLY|O_TRUNC|O_CREAT, 0666) || die $!;
  select OUTFILE;
  print "\#\$ -S \/bin\/bash -cwd -j y -o soe \n\n \.\/aracne $otherarg -r $bs -o ".$dir.$inputfile."_r".$bs3.".adj";
  close OUTFILE;
  system "qsub -p -100 $shfile";
  system "rm ".$shfile;
}


	
