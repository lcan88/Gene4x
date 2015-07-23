function [h_opt output] = findKernelWidth(data, n, h0, varargin)

% FINDKERNELWIDTH finds the optimal kernel width parameter for the
%   Gaussian kernel used by mutual information estimation. This function is 
%   designed for finding a kernel width for a dataset consisting of a large 
%   number of variables, where estimating an optimal kernel width for each 
%   pair of variables is computational intractable. The function finds the 
%   optimal kernel width for a randomly selected set of n pairs of 
%   variables, then uses the mean of the optmial kernel widths for these n 
%   pairs as an approximated kernel width for the entire dataset. For each 
%   pair of variables, the optimal kernel width is determined by maximizing
%   the log-likelihood of the pair of observed data using a Leave-One-Out 
%   crossvaildation approach.
%   
%   Input Arguments
%
%   [H_OPT OUTPUT] = FINDOPTIMALKERNELWIDTH(DATA, N, H0) finds
%   the optimal kernel width, H_OPT on DATA by optimizing on N pairs of 
%   randomly selected varialbes, starting from an initial value H0. The 
%   format of the two required inputs are:
%   
%       DATA    - a N-by-M matrix where genes are in rows and samples in
%       columns. If N=2, users should use instead the function 
%       FINDOPTIMALKERNELWIDTH.
%
%       N       - number randomly selected varialbe pairs to optmize. 
%
%       H0      - an initial value where the optimizer to start. Since we
%       copula transform the data, H0 should be within 0 and 1, and
%       preferrably to the smaller end.
%
%   [...] = FINDKERNELWIDTH(..., 'Options', T) change the options to
%   the optimizer. T must be a cell array of option-value pairs. For
%   example, T can be {'MaxIter', 1000, 'TolFun', 1e-8}. For available
%   options, see help of 'fmincon' and 'optimset' for help. 
%
%   [...] = FINDKERNELWIDTH(..., 'Dispaly', T), where 'T' is either 0 or 1,
%   switch the display message on and off. Default is diplay message on.
%
%   Output Arguments
%
%   The outputs from the function includes:
%       H_OPT   - the optimal kernel width.
%
%       OUTPUT  - structure containing information about the optimization.
%       See help of 'fmincon' for details.
%
%
%   Example:
%
%   h_opt = findOptimalKernelWidth(data, 1000, 0.05);
%
%   [h_opt output] = findOptimalKernelWidth(data, 5000, 0.02, ...
%       'options', {'MaxIter', 1000, 'TolFun', 1e-8})
%
%   See also kernelWidthLOO, fmincon, optimset.
%
%   Copyright 2005 Kai Wang 

% default parameters
msg = 1;
myopts = {};

if nargin < 3
    error('Incorrect number of required arguments to FINDKERNELWIDTH.');
end

if ~isnumeric(n)
    error('N must be a positive integer!');
end
if ~isnumeric(h0)
    error('Kernel width must be numeric!');
end

if rem(nargin-3,2)~=0
    error('Incorrect number of optional arguments to FINDKERNELWIDTH.');
end
okargs = {'options', 'display'};
for it=1:2:length(varargin)
    pname = varargin{it};
    pval = varargin{it+1};
    k = strmatch(lower(pname), okargs);
    if isempty(k)
        error('Unknown parameter name: %s.', pname);
    elseif length(k)>1
        error('Ambiguous parameter name: ', pname);
    else
        switch(k)
            case 1  % options
                if ~iscell(pval)
                    error('Please specify options to the optimizer as a cell array!');
                end
                if rem(length(pval),2)~=0
                    error('Incorrect number of arguments to the optimizer!');
                end
                myopts = pval;
            case 2  % display
                msg = pval;
        end
    end
end

[ngene, narray] = size(data);

[dummy, I] = sort(data,2);
% rank transformation
[dummy, idata] = sort(I,2);
% copula transformation, 1/(2N) left at each boundary
tdata = (idata-0.5)/narray;

oldops = optimset('fminsearch');
% default options to the optimizer    
options = optimset(oldops,'MaxFunEvals',10^3,'MaxIter',10^3, ... 
    'TolX',1e-10,'Display','off');
if (~isempty(myopts)) 
    for i=1:2:length(myopts)
        options = optimset(options, myopts{i}, myopts{i+1});
    end
end

it = 1;
h = zeros(n,1);
while (it <= n)
    if msg
        if (mod(it*100/n,10) == 0), fprintf(1,'%d%s\n', it*100/n, '% completed.'), end
    end
    g1 = round(rand*(ngene-1)+1);
    g2 = round(rand*(ngene-1)+1);
    if (g1 == g2)
        while (g2 == g1) 
            g2 = round(rand*(ngene-1)+1);
        end
    end
    [h_opt fval exitflag]=fminsearch(@(x)(kernelWidthLOO(exp(x), idata([g1 g2],:), tdata([g1 g2],:))), log(h0), options);
    h(it) = exp(h_opt);
    % disp(['H(' num2str(g1) ', ' num2str(g2) ') = ' num2str(h(it))]);
    it = it+1;
end

if (max(h) < 0.5) 
    group = ones(length(h),1);
    center = mean(h);
    h_opt = center;
else
    [group, center] = kmeans(h,2);
    h_opt = min(center);
end

clusterid = unique(group);
clustersize = zeros(length(clusterid),1);
for i=1:length(clusterid)
    clustersize(i) = length(find(group == clusterid(i)));
end

output = struct('numPairs', n, ...
                'clusterCenter', center, ...
                'clusterSize', clustersize, ...
                'hValues', h);

