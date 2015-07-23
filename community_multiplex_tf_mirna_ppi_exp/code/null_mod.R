chr<-as.matrix(read.delim("./msigdb/chrom_filt.txt",,sep="\t",header=FALSE,row.names=1))
cp<-as.matrix(read.delim("./msigdb/cp_filt.txt",,sep="\t",header=FALSE,row.names=1))
bioc<-as.matrix(read.delim("./msigdb/biocarta_filt.txt",,sep="\t",header=FALSE,row.names=1))
kegg<-as.matrix(read.delim("./msigdb/kegg_filt.txt",,sep="\t",header=FALSE,row.names=1))
react<-as.matrix(read.delim("./msigdb/reactome_filt.txt",,sep="\t",header=FALSE,row.names=1))
mirtf<-as.matrix(read.delim("./msigdb/mir_TF_filt.txt",,sep="\t",header=FALSE,row.names=1))
go<-as.matrix(read.delim("./msigdb/GO_filt.txt",,sep="\t",header=FALSE,row.names=1))
geni<-as.matrix(read.delim("./temp/nodi.txt",,sep="\t",header=FALSE))
comm<-as.matrix(read.delim("./temp/comm_filt_genesymbol.txt",,sep="\t",header=FALSE))
th<-numeric(0)
for(cc in 1:dim(comm)[1]){
    nnvuoti<-which(as.matrix((comm[cc,]!=''))==TRUE)
    g<-as.matrix(comm[cc,nnvuoti])
    N<-length(g)
    p_dist<-numeric(0)
    di<-numeric(0)
    for(ran in 1:1000){
        print(ran)
        p1<-numeric(0)
        s<-sample(dim(geni)[1], N , replace=FALSE)
        g<-as.matrix(geni[s,1])
        ####chr
        for(i in 1:dim(chr)[1]){
            nnvuoti<-which(as.matrix((chr[i,]!=''))==TRUE)
            m<-match(as.matrix(chr[i,nnvuoti]),g[,1])
            w<-which(!is.na(m))
            k<-length(w)
            if(k!= 0){
                KK<-length(chr[i,nnvuoti])
                U<-45956
                nn<-dim(g)[1]
                hyp<-1-phyper(k-1,KK,U-KK,nn)
                p1<-rbind(p1,hyp)
            }
        }
        ###cp
        for(i in 1:dim(cp)[1]){
            nnvuoti<-which(as.matrix((cp[i,]!=''))==TRUE)
            m<-match(as.matrix(cp[i,nnvuoti]),g[,1])
            w<-which(!is.na(m))
            k<-length(w)
            if(k!= 0){
                KK<-length(cp[i,nnvuoti])
                U<-45956
                nn<-dim(g)[1]
                hyp<-1-phyper(k-1,KK,U-KK,nn)
                p1<-rbind(p1,hyp)
            }
        }
        ###react
        for(i in 1:dim(react)[1]){
            nnvuoti<-which(as.matrix((react[i,]!=''))==TRUE)
            m<-match(as.matrix(react[i,nnvuoti]),g[,1])
            w<-which(!is.na(m))
            k<-length(w)
            if(k!= 0){
                KK<-length(react[i,nnvuoti])
                U<-45956
                nn<-dim(g)[1]
                hyp<-1-phyper(k-1,KK,U-KK,nn)
                p1<-rbind(p1,hyp)
            }
        }
        ###bioc
        for(i in 1:dim(bioc)[1]){
            nnvuoti<-which(as.matrix((bioc[i,]!=''))==TRUE)
            m<-match(as.matrix(bioc[i,nnvuoti]),g[,1])
            w<-which(!is.na(m))
            k<-length(w)
            if(k!= 0){
                KK<-length(bioc[i,nnvuoti])
                U<-45956
                nn<-dim(g)[1]
                hyp<-1-phyper(k-1,KK,U-KK,nn)
                p1<-rbind(p1,hyp)
            }
        }
        ###kegg
        for(i in 1:dim(kegg)[1]){
            nnvuoti<-which(as.matrix((kegg[i,]!=''))==TRUE)
            m<-match(as.matrix(kegg[i,nnvuoti]),g[,1])
            w<-which(!is.na(m))
            k<-length(w)
            if(k!= 0){
                KK<-length(kegg[i,nnvuoti])
                U<-45956
                nn<-dim(g)[1]
                hyp<-1-phyper(k-1,KK,U-KK,nn)
                p1<-rbind(p1,hyp)
            }
        }
        ###mirtf
        for(i in 1:dim(mirtf)[1]){
            nnvuoti<-which(as.matrix((mirtf[i,]!=''))==TRUE)
            m<-match(as.matrix(mirtf[i,nnvuoti]),g[,1])
            w<-which(!is.na(m))
            k<-length(w)
            if(k!= 0){
                KK<-length(mirtf[i,nnvuoti])
                U<-45956
                nn<-dim(g)[1]
                hyp<-1-phyper(k-1,KK,U-KK,nn)
                p1<-rbind(p1,hyp)
            }
        }
        ###go
        for(i in 1:dim(go)[1]){
            nnvuoti<-which(as.matrix((go[i,]!=''))==TRUE)
            m<-match(as.matrix(go[i,nnvuoti]),g[,1])
            w<-which(!is.na(m))
            k<-length(w)
            if(k!= 0){
                KK<-length(go[i,nnvuoti])
                U<-45956
                nn<-dim(g)[1]
                hyp<-1-phyper(k-1,KK,U-KK,nn)
                p1<-rbind(p1,hyp)
            }
        }
        #q1<-p.adjust(p1, method = "BH", n = length(p1))
        p_dist<-rbind(p_dist,p1)
        di<-rbind(di,dim(p1)[1])
        
        
    }
    q_dist<-p.adjust(p_dist, method = "BH", n = length(p_dist))
    q_dist<-as.matrix(q_dist)
    di<-as.matrix(di)
    qfin<-numeric(0)
    for(sin in 1:dim(di)[1]){
        if(sin!=1){
            prev<-di[sin-1,1]
        }else{
            prev<-0
        }
        qfin<-rbind(qfin,min(q_dist[prev+1:(prev+1+di[sin,1]),1]))
        
    }
    perc<-1-quantile(1-sort(as.numeric(p_dist)), .95)
    th<-rbind(th,perc)
}
write.table(th,'./temp/soglia_enr.txt',col.names=FALSE,row.names=FALSE,sep="\t")
