function [mi, bootstat] = mutualinfo(x, y, h, varargin)
%MUTUALINFO   Mutual information between two vectors.
%   For vectors X and Y, MUTUALINFO(X,Y,H) computes the mutual information
%   between X and Y. This function uses a gaussian kernel methods to
%   estimate the distribution of P(X), P(Y) and P(X,Y), with kernel width H
%   specified by the user.
%   
%   MI = MUTUALINFO(X,Y,H) returns a real number of the mutual information
%   between X and Y using the kernel width H.
%
%   [MI, BOOTSTAT] = MUTUALINFOR(X,Y,H,'BOOTSTRAP',N) returns the mutual
%   information in MI, and performs N bootstrap sampling and a vector of N
%   'MI's estimated from these bootstrapping samples.
%
%   [...] = MUTUALINFO(...,'DISPLAY',T) where T is 'on' or 'off' sets the
%   display mode on and off.
%
%   [...] = MUTUALINFO(...,'CLEARTABLE',T) where T is either 1 or 0,
%   determines whether the global lookup table will be cleared after each
%   MI compuation. The default is to clear the tables, since different MI
%   calculation may use different kernel width. However, in situation when
%   one needs to compute many pairwise MI using the same kernel width, set
%   the cleartable to be 0 will save much compuation time.
%
%   Copyright 2005 Kai Wang 

% default parameters
nboot = 0;
mesg = 0;
cleartable = 1;

if nargin < 3
    error('Incorrect number of required arguments to MUTUALINFO.');
end

if ~(isvector(x) && isvector(y))
    error('First two arguments must be vectors!');
end
if ~isnumeric(h)
    error('Kernel width must be numeric!');
end
if (length(x) ~= length(y))
    error('Vectors must be of the same length!');
end

if rem(nargin-3,2)~=0
    error('Incorrect number of optional arguments to MUTUALINFO.');
end
okargs = {'bootstrap', 'display', 'cleartable'};
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
            case 1  % bootstrap
                if ~isnumeric(pval)
                    error('Please specify the number of boostrapping to perform.');
                else
                    nboot = pval;
                end
            case 2  % display
                if isequal(pval, 'on')
                    mesg = 1;
                elseif isequal(pval, 'off')
                    mesg = 0;
                else
                    warning('Display can be either on or off; it is turned off by default.');
                end
            case 3  % clear table
                cleartable = pval;
        end
    end
end

m = length(x);
% initialize the GLOBAL LOOKUP TABLES
if isempty(whos('global', 'PROB_TABLE'))
    global PROB_TABLE;
    PROB_TABLE = -ones(1,m);
end
if isempty(whos('global', 'NORM_TABLE_1D'))
    global NORM_TABLE_1D;
    SQRT2 = sqrt(2);
    k = ((1:m)-0.5)/m;
    % mass of the 1-D gaussian kernel between [0,1]
    NORM_TABLE_1D = 0.5*( erf((1-k)./(h*SQRT2)) - erf(-k./(h*SQRT2)) );
end

[dummy,Ix] = sort(x);
% rt_x is the rank transformed x
[dummy,rt_x] = sort(Ix);
% xx is the copula transformed x, 1/(2N) left at each boundary
xx = (rt_x-0.5)/m;

[dummy,Iy] = sort(y);
% rt_y is the rank transformed y, 1/(2N) left at each boundary
[dummy,rt_y] = sort(Iy);
%yy is the copula transformed y
yy = (rt_y-0.5)/m;

mi = mutual(rt_x, xx, rt_y, yy, h, m);

if mesg
    fprintf(1,'Mutual Information: %0.6g\n',mi);    % signify 6 digits
end

if isequal(nboot,0) 
    bootstat = [];
else
    if mesg
        disp(['Perform ' num2str(nboot) ' bootstrappings ...']);
    end
    bootstat = zeros(nboot,1);
    for i=1:nboot
        bs = ceil(m*rand(1,m));   % sample with replacement
        bootstat(i) = mutual(rt_x(bs),xx(bs),rt_y(bs),yy(bs),h,m);
        if mesg
            if (mod(i*100/nboot,10) == 0)
                fprintf(1,'%d%s\n', i*100/nboot, '% completed.');
            end
        end
    end
    
    if mesg
        fprintf(1,'\n');
    end
end

if cleartable
    % clear the GLOBAL LOOKUP TABLES
    if ~isempty(whos('global', 'PROB_TABLE'))
        clear global PROB_TABLE;
    end
    if ~isempty(whos('global', 'NORM_TABLE_1D'))
        clear global NORM_TABLE_1D;
    end
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Subfunction to calculate the pairwise mutual information
function minfo = mutual(rt_x, xx, rt_y, yy, h, m)

global PROB_TABLE;
global NORM_TABLE_1D;

% disp(['LOOKUP table has been filled by: ' num2str(sum(sum(PROB_TABLE ~= -1)))]);

ss = 0;
for i=1:m
    fxy = 0;
    fx = 0;
    fy = 0;
    for j=1:m
        % ix, iy are absolute distance in terms of ranks;
        % matlab indices starts from 1, not 0
        ix = abs(rt_x(i)-rt_x(j))+1;
        iy = abs(rt_y(i)-rt_y(j))+1;
        
        dx = xx(i)-xx(j);
        dy = yy(i)-yy(j);
        
        % if (ix, iy) is not computed, at least one of ix and iy is not
        % computed otherwise, all three should have been computed.
        if PROB_TABLE(ix) == -1
            PROB_TABLE(ix) = exp(-(dx*dx)/(2*h*h));
        end
        
        if PROB_TABLE(iy) == -1
            PROB_TABLE(iy) = exp(-(dy*dy)/(2*h*h));  
        end
        
        fx = fx + PROB_TABLE(ix)/NORM_TABLE_1D(rt_x(j));
        fy = fy + PROB_TABLE(iy)/NORM_TABLE_1D(rt_y(j));
        fxy = fxy + PROB_TABLE(ix)*PROB_TABLE(iy)/(NORM_TABLE_1D(rt_x(j))*NORM_TABLE_1D(rt_y(j)));
    end
    ss = ss + log(m*fxy/(fx*fy));
end
    
minfo = max(ss/m, 0);
