#conv<-as.matrix(read.delim('conversion_gene_integer.txt',sep='\t',header=FALSE))
library('GAnet')
##########
combined_db<-as.matrix(ganet.combine(BIOGRID=1,HPRD=1,INTACT=1,MINT=1,Bci=0,UNIHI=0,IMID=1,CELL=1,NAT=1,REACT=1))
p2<-numeric(0)
link<-numeric(0)
nodes<-numeric(0)
density<-numeric(0)
for(i in 0:9){
    #print(i)
    net_new<-as.matrix(read.delim(paste('./temp/test.edgelist_mirna',i,'.txt',sep=''),sep=' ',header=FALSE))
    net_new<-as.matrix(net_new)
    link<-rbind(link,dim(net_new)[1])
    nodes<-rbind(nodes,dim(as.matrix(unique(rbind(as.matrix(net_new[,1]),as.matrix(net_new[,2])))))[1])
    nodi<-as.matrix(unique(rbind(as.matrix(net_new[,1]),as.matrix(net_new[,2]))))
    nUniverse<-round(length(nodi)*(length(nodi)-1)/2)
    m1<-match(combined_db[,1],nodi[,1])
    w1<-which(!is.na(m1))
    m2<-match(as.matrix(combined_db[w1,2]),nodi[,1])
    w2<-which(!is.na(m2))
    nFocusedSet<-as.matrix(combined_db[w2,])
    nFocusedSet<- ganet.UniqNetSimp(nFocusedSet)
    validated<-ganet.ComLinks(netlist=as.matrix(net_new),netdata=as.matrix(combined_db))
    nOverlap<-nrow(validated)
    nPredicted<-nrow(net_new)
    nFocusedSet<-nrow(nFocusedSet)
    fe1 <- nOverlap
    fe2 <- nPredicted - nOverlap
    fe3 <- nFocusedSet - nOverlap
    fe4 <- nUniverse - nPredicted - nFocusedSet + nOverlap 
    FisherData <- matrix(c(fe1, fe2, fe3, fe4), ncol=2, byrow=T)
    p2 <- rbind(p2,fisher.test(FisherData)$p.value)
    density<-rbind(density,(as.numeric(link[i+1,1])/complete))
}
alpha<-as.matrix(c(0.005,0.01,0.02,0.03,0.05,0.1,0.2,0.3,0.4,0.5))
final<-cbind(alpha,link,p2,nodes,density)
colnames(final)<-c('alpha','Links','p-value','nodes','density')
write.table(final,'./temp/selection_links_MIRNA.txt',sep='\t',row.names=FALSE)

