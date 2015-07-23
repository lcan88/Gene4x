#!/usr/bin/perl -w

#####################################################################
#                                                                   #
# Program to concatenate adj files into a single adj file           #
#                                                                   #
# Usage:  concatadj.pl  <adj dir>                                   #
# Example:  concatadj.pl adj/                                       #
#                                                                   #
# Description: This program read all adj files in <adj dir> and     #
#   concatenate them into a single adj file. Typically used after   #
#   parallel computing by splitting ARACNE jobs using -h/-s option. #
#   Please make sure all the adj files in <adj dir> are created     #
#   using the same ARACNE parameters.                               #
#                                                                   #
#####################################################################

use Fcntl;
$dir = $ARGV[0]; 
opendir(DH,$dir) or die "Error, could not open $dir directory\n";
@adjfile = readdir DH;
@adjfile = grep !/^\./, @adjfile;
@adjfile = grep /\.adj$/, @adjfile;
@adjfile = map {$_=$dir.$_} @adjfile;
$adjfile[0] =~ m/^${dir}(\S+)_h\S+?(_[k|t|p]\S+)/;
$newadj = $1.$2;

sysopen(OUT, $newadj, O_WRONLY|O_TRUNC|O_CREAT, 0666) || die $!;
foreach $adjfile (@adjfile){
  open(FH,$adjfile) or die "Error, could not open $adjfile file\n";
  while(<FH>) {
    next if ($_ =~ m/^>/);
    chomp;
    print OUT $_."\n";
  }
  close FH;
}
close OUT;
	
