#!/bin/bash
#SBATCH --job-name=aw
#SBATCH --nodes=1
#SBATCH --time=72:00:00
#SBATCH --output=job_output/%j-out
#SBATCH -p bii
#SBATCH --mem=8000

module load anaconda
module load cairo
source activate graph-plot


for graph in $(ls input_nets/*.edges)
do 
   echo $graph
   echo $( basename $graph)
   if [ $1 = "1" ]
   then
   python plot_graph.py --contract "True" --drop_isolates "True" --input_path "${graph}" --output_path "demo_net_plots/$( basename $graph).no_iso.contract.pdf" --scale "True" -algo "auto"
   else
   python plot_graph.py --contract "True" --input_path "${graph}" --drop_isolates "True" --output_path "demo_net_plots/$( basename $graph).no_iso.contract.pdf" -algo "auto"
   fi
done 
