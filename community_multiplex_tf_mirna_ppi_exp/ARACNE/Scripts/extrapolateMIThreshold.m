function [threshold coef] = extrapolateMIThreshold(data, h, n, varargin)

% EXTRAPOLATEMITHRESHOLD extrapolates mutual information (MI) thresholds for 
% different statistical significance (p-values) under the the null 
% hypotheis of mutually independent variables.
%
%   [THRESHOLD COEF] = EXTRAPOLATEMITHRESHOLD(DATA, H, N) computes N 
%   pairwise MI between randomly selected variables whose sample labels are
%   permutated at random. It is then used as a null distribution for 
%   assessing the statistical significance of MI computed from the same
%   dataset. By large deviation theory, the right tail of this null
%   distriubtion should decay exponentially, which can be used to
%   extrapolate to arbitrarily small p-values.
%       DATA        - a N-by-M matrix where variables are in rows and 
%                     samples in columns.
%
%       N           - number randomly selected varialbe pairs to compute 
%                     under null
%
%       H           - kernel width used in MI calculation.
% 
%       THRESHOLD   - MI threshold determined for p-values of 
%                     {5E-1, 2E-1, 1E-1, 5E-2, 2E-2 1E-2, ..., 1E-20}
%
%       COEF        - coefficient of the extrapolation function fitted to
%                     the right tail of the null distribution
%
%   [THRESHOLD COEF] = EXTRAPOLATEMITHRESHOLD(DATA,H)N,'PLOT',T), where 'T=1' 
%   displays a plot of the extrapolation; 'T=0' otherwise.  
%

graphplot = 0;

if nargin < 3
    error('Incorrect number of arguments to PVALUETABLE.');
end
if rem(nargin-3,2)~=0
    error('Incorrect number of optional arguments to PVALUETABLE.');
end

okargs = {'plot'};
for it=1:2:length(varargin)
    paraname = varargin{it};
    paraval = varargin{it+1};
    k = strmatch(lower(paraname), okargs);
    if isempty(k)
        error('Unknown parameter name: %s.', paraname);
    else
        if ~isnumeric(paraval)
            error('Display can be either 1 or off 0.');
        else
            graphplot = paraval;
        end
    end
end


% permute the genes to generate random pairs
% reset the random number generator back to the same initial state
rand('state', 0); 
[ngene, narray] = size(data);
rdata = zeros(ngene,narray);
for i=1:narray
    rdata(:,i) = data(randperm(ngene)',i);
end

% compute null MI 
it = 1;
null = zeros(n,1);
while (it <= n) 
    i = round(rand*(ngene-1)+1);
    j = round(rand*(ngene-1)+1);
    if (i == j)
        while (j == i) 
            j=round(rand*(ngene-1)+1);
        end
    end
    null(it) =  mutualinfo(rdata(i,:), rdata(j,:), h);
    it = it+1;
end    

[b a] = hist(null, 100);
b = b./n;

% select region and fit a linear line
lfitix = find(b==max(b));
lfitix = lfitix(length(lfitix));  %make sure there is only one peak
lfit = a(ceil(.2*(100-lfitix))+lfitix);
hfit = a(floor(.5*(100-lfitix))+lfitix);
i = find(a>=lfit & a<=hfit);
coef = polyfit(a(i), log(b(i)), 1);

% plot graph
if (graphplot==1)
    figure;
    subplot(2,2,1);
    plot(a, b);
    title('Histogram');
    xlabel('Mutual Information');
    subplot(2,2,2);
    semilogy(a, b);
    xlabel('Mutual Information');
    title('Semilogy');
    subplot(2,2,3);
    semilogy(a, b, ':');
    xlabel('Mutual Information');
    title('Extraploation');
    hold on;
    semilogy(a(i), b(i), 'LineWidth', 2);
    plot(a, exp(a*coef(1)+coef(2)), 'r');
    set(gca, 'ylim', [0.00001,1], 'xlim', [0 max(null)]);
    plot([lfit,lfit], get(gca, 'ylim'), 'r-.');
    plot([hfit,hfit], get(gca, 'ylim'), 'r-.');
    hold off;
end

%compute p-value vs thresholds
p=20; 
decimal = [5 2 1];
null_sorted = sort(null);
threshold = zeros(p*length(decimal),1);

for j=1:p
    for k = 1:length(decimal)
        pv = decimal(k)*10^(-j);
        % since we compute only 1e+5 mutual informations, the p-values can only
        % be accurately estimate up to 1e-4; the rest need to extrapolate
        if (j<5)    
            threshold(3*(j-1)+k) = null_sorted(ceil((1-pv)*n));
        else
            threshold(3*(j-1)+k) = (log(pv)-coef(2))/coef(1);
        end
    end
end