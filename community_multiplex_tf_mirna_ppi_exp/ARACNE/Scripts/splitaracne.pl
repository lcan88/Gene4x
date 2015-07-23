#!/usr/bin/perl -w

##########################################################################
#                                                                        #
# Program to split aracne jobs and submit them to cluster running the    #
#   Sun Grid Engine job scheduler                                        #
#                                                                        #
# Usage: splitaracne.pl  <ARACNE arguments>                              #
# Example: splitaracne.pl -i Bcell.exp -l tf.txt -s probe.txt -p 1e-6    #
#                                                                        #
# Description: This program first read the probe file and submit each    #
#   probe as a single ARACNE job using -h option. It makes use of the    #
#   computer clusters for parallel computing to shorten the time needed  #
#   to compute ARACNE networks. Please make sure you have the ARACNE     #
#   executable & configuration files in your current working directory.  #
#   Upon finish running the jobs, use script concatadj.pl to concatenate #
#   all the adj files into a single file.                                #
#                                                                        #
##########################################################################

use Fcntl;
$allarg="@ARGV"; 
@probelist=();
if ($allarg =~ m/(.*)\-s\s+(\S+)\s(.*)/) {
  $probefile = $2;
  $arg = $1.$3;
  open(FH, $probefile) or die "Error, could not open $probefile file\n";
  while(<FH>) {
    chomp;
    $_=~s/\s//;
    push(@probelist,$_) unless ($_ eq "");
  }
  close FH;
} else {
  $allarg =~ m/(.*\-i\s+)(\S+)(\s.*)/;
  $expfile = $2;
  $arg = $1.$2.$3;
  open(FH, $expfile) or die "Error, could not open $expfile file\n";
  $header=<FH>; $header="";
  while(<FH>) {
    $_ =~ m/^\s*(\S+)\s/;
    push(@probelist,$1);
  }
  close FH;
}

foreach $probeid (@probelist){
  $shfile = "aracne_".$probeid.".sh";
  sysopen(OUT, $shfile, O_WRONLY|O_TRUNC|O_CREAT, 0666) || die $!;
  print OUT "\#\$ -S \/bin\/bash -cwd -j y -o soe \n\n\.\/aracne $arg -h $probeid";
  close OUT;
  system "qsub -p -100 $shfile";
  system "rm ".$shfile;
}


	
