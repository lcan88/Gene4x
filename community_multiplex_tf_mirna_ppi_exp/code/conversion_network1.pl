#!/usr/bin/perl
use Getopt::Long;
open (STDERR, ">>log_genes_extraction.txt") or die "Can't open STDERR: $!";

my $input;
my $output;


GetOptions ('g1=s' => \$input,
            'g2=s' => \$output,
            
);





open (FILE1, "<".$input) || die "Cannot open '$data' in reading mode:error $!";
open (FILE2, ">>".$output) || die "Cannot open '$out' in writing mode:error $!";
while (my $row = <FILE1>)
{
	chomp($row);
        my $i=0;
        @row_split =split('\t', $row);
        my $number = @row_split+1;
        my $number2 = $number/2;
        for ($i = 0; $i <= $number2-2; $i++)
        {

     print FILE2 ("@row_split[0]\t@row_split[(2*$i+1)]\t@row_split[(2*$i+2)]\n");
        }

}
