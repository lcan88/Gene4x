#!/usr/bin/perl -w

######################################################################
#                                                                    #
# Program to merge probes representing the same gene in adj file     #
#                                                                    #
# Usage:  fuseprobe.pl  <exp file>  <adj file>                       #
# Example:  fuseprobe.pl data.exp data_k0.1_t0.1_e0.1.adj            #
#                                                                    #
# Description: This program create a new adj file by merging probes  #
#   that represent the same gene, based on the annotation of gene    #
#   symbol in the second column of the exp file. In addition to      #
#   merging probes, it will also filter probes with multiple gene    #
#   symbols ("abc///def") or without gene symbol ("---"), and rank   #
#   the output hubs by size and neighbors by MI values               #
# Output: <adj filename>_fusedprobe.adj                              #
#                                                                    #
######################################################################

use Fcntl;
($expfile,$adjfile) = @ARGV;
$adjfile =~ m/^(\S+)(.adj)/; 
$outfile = $1."_fusedprobe".$2;


#---create ix/id/sym hash tables---# 
open(FH, $expfile) or die "Error, could not open $expfile file\n";
%ix2id=(); %ix2sym=(); %id2sym=();
$ixcount=0;
while(<FH>){
  next if ($_ =~ m/^AffyID/i);
  $_ =~ m/^(\S+)\s+(\S+)\s+/;
  $ix2id{$ixcount} = $1;
  $ix2sym{$ixcount} = $2;
  $id2sym{$1} = $2;
  ${ixcount}++;
}
close FH;


#---fuse probe---#
open(FH, $adjfile) or die "Error, could not open $adjfile\n";
%neighborsize=(); 
while (<FH>) {
  next if ($_ =~ m/^>/);
  ($id,$geneline) = $_ =~ m/^\s*(\S+)\s+(\S.*)/;
  next if ($id2sym{$id} =~ m/---/);
  next if ($id2sym{$id} =~ m/\//);
  @targene = $geneline =~ m/(\S+)\s+(\S+\.\S+)\s/g;

  for ($i=0; $i<scalar(@targene); $i+=2) {
    $tarid = $targene[$i];
    $tarmi = $targene[$i+1];
    next if ($id2sym{$tarid} eq "---");
    next if ($id2sym{$tarid} =~ m/\//);
    next if ($id2sym{$id} eq $id2sym{$tarid});

    if (exists ${$id2sym{$id}}{$id2sym{$tarid}}) {
      ${$id2sym{$id}}{$id2sym{$tarid}} = $tarmi if ($tarmi > ${$id2sym{$id}}{$id2sym{$tarid}});
    } else {
      ${$id2sym{$id}}{$id2sym{$tarid}} = $tarmi;
    }
  }
  $neighborsize{$id2sym{$id}} = keys(%{$id2sym{$id}});
}
close FH;


#---print output---#  
sysopen(OUT, $outfile, O_WRONLY|O_TRUNC|O_CREAT, 0666) || die $!;
@sortedhub = sort {$neighborsize{$b} <=> $neighborsize{$a}} keys %neighborsize;
foreach $hubsym (@sortedhub){
  print OUT $hubsym;
  @sortedtar = sort {${$hubsym}{$b} <=> ${$hubsym}{$a}} keys %$hubsym;
  foreach $tarsym (@sortedtar) {
    print OUT "\t".$tarsym."\t".${$hubsym}{$tarsym};
  }
  print OUT "\n";
}
close OUT;

