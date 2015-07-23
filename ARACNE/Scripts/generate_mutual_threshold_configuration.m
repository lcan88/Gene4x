% This script generates the ARACNE configuration file - "config_threshold.txt"
%
% It assumes data has been loaded into MATLAB workspace in a variable
% called 'data', in which rows represent variables and columns samples. 
%
% This script require the following function in the MATLAB path or in the
% current working directory: extrapolateMIThreshold.m
%
% Note: finding the MI threshold for a dataset can take some amount
% of time. In our B-cell application, we determined the kernel width for
% sample size of {100, 120, ..., 360}, which took ~6 hours to complete on
% a desktop PC with Pentimum 4 processor.

[ngene narray] = size(data);
m = 100:20:narray;  % range of sample size
rep = 3;            % number of replicate for each sample size
n = 100000;         % number of null MI to compute

outpval = fopen('pvalue.txt', 'w');
outcoef = fopen('coef.txt', 'w');

p=20; 
decimal = [5 2 1];
fprintf(outpval, '%s\t', 'samplesize');
for j=1:p
    for k=1:length(decimal)
        fprintf(outpval, '%s\t', [num2str(decimal(k)) 'E-' num2str(j)]);
    end
end  
fprintf(outpval, '\n');
fprintf(outcoef, '%s\t%s\t%s\n', 'samplesize', 'gradient', 'intercept');

gradvector=[];
for i=1:length(m)
    fprintf(1,'%s%d\n', 'sample size: ', m(i));
    % find kernel width
    h = -0.025 + 1.72*(m(i)^(-.2))*sqrt((m(i)+1)/(12*m(i)));
    gradavg(i,:)=[m(i) 0];
    for j=1:rep
        rand('state', sum(100*clock)); 
        idx = randsample(size(data,2),m(i));
        datam = data(:,idx);
        % generate p-value determined MI values 
        [threshold coef] = extrapolateMIThreshold(datam,h,n,'plot',0);
        fprintf(outcoef, '%s\t%f\t%f\n', [num2str(m(i)) 'rep' num2str(j)], coef(1), coef(2));
        fprintf(outpval, '%s\t', [num2str(m(i)) 'rep' num2str(j)]);
        for k=1:length(threshold)
            fprintf(outpval, '%f\t', threshold(k));
        end
        fprintf(outpval, '\n');
        gradavg(i,2) = gradavg(i,2) + coef(1);
        gradvector = [gradvector; [m(i) coef(1) coef(2)]];
    end
    gradavg(i,2) = gradavg(i,2)/rep;
end

fclose(outpval);
fclose(outcoef);

figure,
plot(gradvector(:,1), gradvector(:,2), '.', 'LineWidth', 2);
hold on;
xlabel('Sample Size', 'FontSize', 16);
ylabel('Slope', 'FontSize', 16);
set(gca,'FontSize', 16, 'LineWidth', 2);

coef = polyfit(gradavg(:,1), gradavg(:,2), 1);
plot(gradavg(:,1), gradavg(:,1)*coef(1)+coef(2), 'r', 'LineWidth', 2);
m=sprintf('%.3f', coef(1));
c=sprintf('%.2f', coef(2));
legend('observed gradient', ['y = ' num2str(m) 'x ' num2str(c)]);

out = fopen('config_threshold.txt', 'w');
fprintf(out, '%g\t%g\t%g', mean(gradvector(:,3)), coef(2), coef(1));
fclose(out);

