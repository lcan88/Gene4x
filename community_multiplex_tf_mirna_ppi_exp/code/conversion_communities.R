library('BioPhysConnectoR')
X<-as.matrix(read.delim('./temp/comm_filt.txt',sep='\t',header=FALSE))
d<-numeric(0)
for(l in 1:dim(X)[1]){   
    w<-which(as.matrix((X[l,]!='NA'))==TRUE)
    d<-rbind(d,length(w))
}
d<-as.matrix(d)
X<-cbind(as.numeric(d),X)
X<-as.matrix(mat.sort(X, sort=1 , decreasing = TRUE))
X<-X[,-1]
Y <- matrix(data=NA,nrow=dim(X)[1],ncol=dim(X)[2])
conversion<-as.matrix(read.delim("./temp/nodes.txt", header = FALSE, sep = "\t"))
for (j in 1:dim(X)[2]){
  for(i in 1:dim(X)[1]){
    if(!is.na(X[i,j])){
      Y[i,j]<-conversion[as.numeric(X[i,j]),1]
    }
    if(is.na(X[i,j])){
        Y[i,j] <- ''
    }
  }
} 
xx<-as.matrix(1:dim(X)[1])
final<-cbind(xx,Y)
write.table(final, './comm_final_genesymbol.txt', sep='\t', row.names=FALSE, col.names=FALSE)
