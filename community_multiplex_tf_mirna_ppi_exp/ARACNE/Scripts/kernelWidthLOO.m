function logl=kernelWidthLOO(h,idata,tdata)
% Compute the log-likelihood for the entire dataset. 
% 	h       -   the kernel width
%   idata   -   data in same format as that for tdata, but each variable is
%               rank transformed.
%   tdata   -   data in matrix format, where rows are variables and columns
%               are samples. each variable is copula transformed.
% 
% If tdata and idata contains two variables (i.e. two rows), the
% log-likelihood is computed for the pair of variable. If more than two
% varialbes prsent (i.e. more than 2 rows), the loglikelihood will be
% computed using all variables in the data by 1) pair up the varialbes
% sequencially, e.g. (v1, v2), (v3, v4), ... 2) sum up the log-likelihood
% for each pair of varialbes.
%
% Copyright 2005 Kai Wang 


% idata is the rank transformed data: it contians the rank of each data
% point, thus consisiting all integers.
% tdata is the copula transformed data, i.e. scale idata to be between 0
% and 1
% global tdata idata;
[n,m] = size(tdata);

% lookup table for computing densities, which depends only on the distance
% (in terms of ranks) to the center of the gaussian kernel
P_TABLE = -ones(1,m);

SQRT2 = sqrt(2);
% lookup table for computing the normalization factors at each gaussian
% kernel, which depends only on the position of the kernel (in terms of
% ranks).
k = ((1:m)-0.5)/m;
N_TABLE = 0.5*( erf((1-k)./(h*SQRT2)) - erf(-k./(h*SQRT2)) );

logl=0;

p = 1;
while (p+1 <= n)
    ll = 0; % log-likelihood for each pair of variables
    for i=1:m   
        l=1e-323;
        for j=1:m
            if (j==i) 
                continue; 
            end
        
            dx = abs(idata(p,i) - idata(p,j))+1;
            dy = abs(idata(p+1,i) - idata(p+1,j))+1;
            
            if P_TABLE(dx) == -1
                P_TABLE(dx) = exp(-(tdata(p,i)-tdata(p,j))^2/(2*h*h));
            end
            
            if P_TABLE(dy) == -1
                P_TABLE(dy) = exp(-(tdata(p+1,i)-tdata(p+1,j))^2/(2*h*h));
            end
            
            l = l + P_TABLE(dx)*P_TABLE(dy)/(N_TABLE(idata(p,j))*N_TABLE(idata(p+1,j)));
        end
        ll = ll + log(l) - 2*log(h) - log(2*pi*(m-1));
    end
    p = p+2;
    logl = logl + ll;
end

logl=-(logl + log(2/(pi*(1+h*h))));

