/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author DLiu1
 */
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.util.CellRangeAddress;

public class CellProperties {
        private int colspan =0;
        private int rowspan =0;
        private Boolean first = false;
        private Boolean inRange = false;
        private CellRangeAddress range;
        private HSSFCell cell = null;
        
        public int getColspan() {
                return colspan;
        }
        
        public int getRowspan() {
                return rowspan;
        }
        
        public Boolean isFirst(){
                return first;
        }
        
        public Boolean isInRange(){
                return inRange;
        }
        
        public CellRangeAddress getRange() {
                return range;
        }
        
        public int getRangeWidth(){
                int res = 0;            
                for(int i=range.getFirstColumn(); i<=range.getLastColumn(); i++){
                        int w = cell.getSheet().getColumnWidth(i)/32;
                        res+=w;
                }               
                return res;
        }
        
        public int getRangeHeigt(){
                int res = 0;            
                for(int i=range.getFirstRow(); i<=range.getLastRow(); i++){                     
                        int h = cell.getSheet().getColumnWidth(i)/32;
                        HSSFRow  r =  cell.getSheet().getRow(i);
                        if(r!=null){
                                h = r.getHeight()/16+2;
                        }
                        res+=h;
                }               
                return res;
        }
        
        
        
        
//      public CellProperties(Boolean in, Boolean first, int colspan, int rowspan, CellRangeAddress range){
//              this.inRange = in;
//              this.first = first;
//              this.colspan = colspan;
//              this.rowspan = rowspan;
//              this.range = range;
//      }
        
        public CellProperties(HSSFCell cell){
                this.cell  =cell;
                inRange = false;
                first  =false;
                colspan = 0;
                rowspan = 0;
                CellRangeAddress rng = null;
                int num = cell.getSheet().getNumMergedRegions();
                for(int i=0; i<num; i++){
                        rng = cell.getSheet().getMergedRegion(i);
                        if(cell.getRowIndex()>=rng.getFirstRow() 
                                        && cell.getRowIndex()<=rng.getLastRow()
                                        && cell.getColumnIndex()>=rng.getFirstColumn()
                                        && cell.getColumnIndex()<=rng.getLastColumn()){
                                
                                inRange = true;
                                rowspan = rng.getLastRow() - rng.getFirstRow()+1;
                                colspan = rng.getLastColumn() - rng.getFirstColumn()+1;
                                if(rng.getFirstColumn()== cell.getColumnIndex() && rng.getFirstRow() == cell.getRowIndex()){
                                        first = true;
                                }
                                break;                          
                        }
                }
                range = rng;
        }

}
