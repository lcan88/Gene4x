#!/usr/bin/perl -w
use Fcntl;


@pValues = (1e-11, 1e-10, 1e-9, 1e-8, 1e-7, 1e-6, 1e-5, 1e-4, 1e-3, 1e-2);
@toleranceValues = (0.0, 0.05, 0.10, 0.15);

system "mkdir \.\/ARACNE_results";

foreach $pValue (@pValues) {
	foreach $toleranceValue (@toleranceValues) {
		$shfile = "aracne_pValue_".$pValue."_tolerance_".$toleranceValue.".sh";

		sysopen(OUT, $shfile, O_WRONLY|O_TRUNC|O_CREAT, 0666) || die $!;


		$command = "\.\/aracne -i \.\/Data/BCell_matrix.exp -o \.\/ARACNE_results/MYC_network_p_".$pValue."_e_".$toleranceValue.".adj".
			" -s \.\/Data/myc_probes.txt -l \.\/Data/U95A_TFs.txt -p ".$pValue." -e ".$toleranceValue;

		print OUT "\#\$ -S \/bin\/bash \n\n";
		print OUT $command;
	    close OUT;
	    system "qsub -p -100 -V $shfile";
	    system "rm ".$shfile;
	}
}
