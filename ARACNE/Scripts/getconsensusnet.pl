#!/usr/bin/perl -w

############################################################################
#                                                                          #
# Program to construct consensus network                                   # 
#                                                                          #
# Usage:  getconsensusnet.pl <adjdir> <threshold p-value>                  #
# Example:  getconsensusnet.pl adjdir/ 1e-6                                #
#                                                                          #
# Description: This program combine all bootstrap networks (adj files) in  #
#   the directory <adjdir> specified by the user, and output a consensus   #
#   network preserving edges with statistically significant number of      #
#   supports. Hypotheis tests are constructed in which a null distribution #
#   of edge supports is derived by randomly permuting the inferred edges   #
#   in each bootstrapping networks.                                        #
#                                                                          #
# Output1: filename_pvalue.adj (the consensus network)                     #
# Output2: filename_info.adj (statistics of the bootstrapping networks)    #
# Dependency: library Statistics::Distributions (please modify the first   #
#   line of the script to set the path to the library location             #
#                                                                          #
############################################################################

use lib "/users2/wl2131/shared/perl/lib/perl5/site_perl/5.8.0";
use Statistics::Distributions;
use Fcntl;


#-----parsing input arguments-----#

if (scalar(@ARGV) < 2) {
  die "Incorrect number of arguments!\nUsage:  getconsensusnet.pl <adjdir> <threshold p-value>\nExample:  getconsensusnet.pl adjdir/ 1e-6 \n";
}

($dir,$pthres) = @ARGV;
$dir=$dir."\/" unless ($dir=~m/\/$/);
opendir(DH,$dir) or die "Error, could not open $dir directory\n";
@filename = readdir(DH);
@filename = grep !/^\./, @filename; 
@filename = grep /\.adj$/, @filename;
@filename = map "$dir$_", @filename;
closedir(DH);
$filename[0] =~ m/$dir(\S+)_r\S+/;
$adjname = $1;
$totbs = scalar(@filename);


#-----summarize bootstrap adj files-----#

%totSupport=();
%totEdge=();
%totMI=();
$header="";
$bsnum=1;

foreach my $filename (@filename){
  $totEdge{$bsnum}=0;
  open(FH, $filename) or die "Error, could not open $filename\n";
  while (<FH>){
    if ($_ =~ m/\>/) {
      $header = $header.$_ if ($bsnum eq 1);
      next;
    }
    ($hubid,$geneline) = $_ =~ m/^(\S+)\s+(\S.*)/;
    @gene2s = $geneline =~ m/(\S+)\s+(\S+\.\S+)/g;
    $genelen = @gene2s;
    for ($i=0; $i<(${genelen}-1); $i=$i+2){
      my $key = "${hubid}\.$gene2s[$i]";
      if (defined $totSupport{$key}){
	$totSupport{$key}++;
	$totMI{$key} = $totMI{$key} + $gene2s[$i+1];
      }
      else{
	$totSupport{$key} = 1;
	$totMI{$key} = $gene2s[$i+1];
      }
      $totEdge{$bsnum}++;
    }
  }
  close FH;
  $bsnum++;
}


#-----print info: Bonferroni corrected p-value, mean, std dev-----#

my $outfile = ${adjname}."_bs".${totbs}."_info.txt";
sysopen(OUT, $outfile, O_WRONLY|O_APPEND|O_CREAT, 0666) || die $!;

my $totedge = scalar keys(%totSupport);
print OUT "total edges tested: $totedge \n";

$balpha = sprintf("%.2e", 0.05/$totedge);
print OUT "Bonferroni corrected (0.05) alpha: $balpha \n";

$mu=0; $sigma=0;
foreach my $bs (1..$totbs) {
  $prob = $totEdge{$bs} / scalar keys(%totSupport);
  $mu += $prob;
  $sigma += $prob*(1-$prob);
}
$sigma = sqrt($sigma);
print OUT "mu: $mu\nsigma: $sigma \n";

close OUT;


#-----output consensus network-----#

my $ealpha = sprintf("%.0e", $pthres);
$outfile = ${adjname}."_bs".${totbs}."_p".${ealpha}.".adj";
sysopen(OUT, $outfile, O_WRONLY|O_TRUNC|O_CREAT, 0666) || die $!;
$currentg1 = -1;
print OUT $header;
foreach $key (sort keys %totSupport) {
  ($g1,$g2) = $key =~ m/(\S+)\.(\S+)/; 
  my $z = ($totSupport{$key}-$mu)/${sigma}; 
  $pval = sprintf("%0.1e", Statistics::Distributions::uprob($z));
  if ($pval < $pthres) {
    if ($g1 ne $currentg1) {
      print OUT "\n" unless ($currentg1 eq -1);
      print OUT $g1,"\t";
      $currentg1 = $g1; 
    }
    my $mi = sprintf("%.4f", $totMI{$key}/$totSupport{$key});
    print OUT $g2,"\t",$mi,"\t";
  }
}
close OUT;

