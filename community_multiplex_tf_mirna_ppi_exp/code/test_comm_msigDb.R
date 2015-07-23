chr<-as.matrix(read.delim("./msigdb/chrom_filt.txt",,sep="\t",header=FALSE,row.names=1))
cp<-as.matrix(read.delim("./msigdb/cp_filt.txt",,sep="\t",header=FALSE,row.names=1))
bioc<-as.matrix(read.delim("./msigdb/biocarta_filt.txt",,sep="\t",header=FALSE,row.names=1))
kegg<-as.matrix(read.delim("./msigdb/kegg_filt.txt",,sep="\t",header=FALSE,row.names=1))
react<-as.matrix(read.delim("./msigdb/reactome_filt.txt",,sep="\t",header=FALSE,row.names=1))
mirtf<-as.matrix(read.delim("./msigdb/mir_TF_filt.txt",,sep="\t",header=FALSE,row.names=1))
go<-as.matrix(read.delim("./msigdb/GO_filt.txt",,sep="\t",header=FALSE,row.names=1))
comm<-as.matrix(read.delim("./temp/comm_filt_genesymbol.txt",,sep="\t",header=FALSE))
soglie<-as.matrix(read.delim("./temp/soglia_enr.txt",,sep="\t",header=FALSE))
for(cc in 1:dim(comm)[1]){
	soglia<-soglie[cc,1]
   p1<-numeric(0)
   nnvuoti<-which(as.matrix((comm[cc,]!=''))==TRUE)
   g<-as.matrix(comm[cc,nnvuoti])
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
           p1<-rbind(p1,cbind(row.names(chr)[i],hyp))
   
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
           p1<-rbind(p1,cbind(row.names(cp)[i],hyp))
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
           p1<-rbind(p1,cbind(row.names(react)[i],hyp))
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
           p1<-rbind(p1,cbind(row.names(bioc)[i],hyp))
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
           p1<-rbind(p1,cbind(row.names(kegg)[i],hyp))
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
           p1<-rbind(p1,cbind(row.names(mirtf)[i],hyp))
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
           p1<-rbind(p1,cbind(row.names(go)[i],hyp))
       }
   }
   q1<-p.adjust(p1[,2], method = "BH", n = length(p1))
   q1<-as.matrix(cbind(as.matrix(p1[,1]),as.matrix(q1)))
   w<-which(as.numeric(q1[,2])<soglia)
   q1_fin<-as.matrix(q1[w,])
   if(dim(q1_fin)[2]==1){
       q1_fin<-t(q1_fin)
   }
   write.table(q1_fin,paste("comm_enrichment_results/comm_",cc,".txt",sep=""),col.names=TRUE,row.names=FALSE,sep="\t")
}