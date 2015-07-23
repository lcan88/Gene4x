% This script generates the ARACNE configuration file - "config_kernel.txt"
%
% It assumes data has been loaded into MATLAB workspace in a variable
% called 'data', in which rows represent variables and columns samples. 
%
% This script require the following function in the MATLAB path or in the
% current working directory:
% 1) findKernelWidth.m
% 2) kernelWidthLOO.m
%
% Note: finding the optimal kernel width for a dataset can take some amount
% of time. In our B-cell application, we determined the kernel width for
% sample size of {100, 120, ..., 360}, which took ~24 hours to complete on
% a desktop PC with Pentimum 4 processor.

[ngene narray] = size(data);

% find the kernel width for sub-dataset of sizes starting from 100 (which 
% is a minimal sample size requirement of ARACNE) with an increment of 20
% at each step. For each sample size, randomly select 3 sets of samples and
% use the average kernel width
n = (100:20:narray)';

result = zeros(length(n),3);
for i=1:length(n)
    disp(['Sample Size = ' num2str(n(i))]);
    for j=1:3
        disp([' Repeat ' num2str(j)]);
        idx = randsample(narray,n(i));
        subset = data(:,idx);
        result(i,j) = findKernelWidth(subset, 1000, 0.1);
    end
end

% take the average kernel width for each sample size
h_bar = mean(result,2);

% fit the function h = alpha*n^beta to the kernel profile
% In the log-log space, this is equivalent as fitting a linear line to
% log(h) ~ log(n)
xx = [ones(length(n),1) log(n)];
yy = log(h_bar);
coef = inv(xx'*xx)*xx'*yy;

% generate a plot to assess the goodness of the fitting
figure;
plot(n, h_bar, 'bo');
hold on;
a = 100:360;
plot(a, exp(coef(1))*a.^coef(2), 'r-', 'LineWidth', 2);
ylabel('Kernel Width','FontSize',16);
xlabel('Sample Size','FontSize',16);
legend('Observed',['h=' num2str(exp(coef(1))) '\timesn^{' num2str(coef(2)) '}']);
set(gca, 'FontSize', 16)

% output the parameters to the ARACNE configuration file
out = fopen('config_kernel.txt','w');
fprintf(out, '%g\t%g', exp(coef(1)), coef(2));
fclose(out);

