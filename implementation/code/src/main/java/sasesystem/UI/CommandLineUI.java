/*
 * Copyright (c) 2011, Regents of the University of Massachusetts Amherst 
 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:

 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 * 		and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 * 		and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *   * Neither the name of the University of Massachusetts Amherst nor the names of its contributors 
 * 		may be used to endorse or promote products derived from this software without specific prior written 
 * 		permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF 
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package sasesystem.UI;

import sasesystem.engine.ConfigFlags;
import sasesystem.engine.EngineController;
import sasesystem.engine.Profiling;
import sasesystem.stream.ParseStockStreamConfig;
import sasesystem.stream.StockStreamConfig;
import sasesystem.stream.StreamController;
import net.sourceforge.jeval.EvaluationException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;


/**
 * The interface
 * @author haopeng
 *
 */
public class CommandLineUI {
	/**
	 * The main entry to run the engine under command line
	 * 
	 * @param args the inputs 
	 * 0: the nfa file location 
	 * 1: the stream config file
	 * 2: print the results or not (1 for print, 0 for not print)
	 * 3: use sharing techniques or not, ("sharingengine" for use, nothing for not use)
	 */
	public static void main(String args[]) throws CloneNotSupportedException, EvaluationException, IOException{

		String parrent_dir = Paths.get(System.getProperty("user.dir")).getParent()+"/";

		String nfaFileLocation = "test.query";
		String streamConfigFile = "test.stream";
		String inputFile = "test.stream";
		String eventtype = "stock";

		
		String engineType = null;
		if(args.length > 0){
			nfaFileLocation = parrent_dir+args[0];
		}
		
		if(args.length > 1){
			streamConfigFile = parrent_dir+args[1];
		}

		if(args.length > 2){
			inputFile = parrent_dir+args[2];
			ConfigFlags.inputFile = inputFile;
		}

		if(args.length > 3){
			ConfigFlags.engine = args[3];
		}

		if(args.length > 4){
			if(Integer.parseInt(args[4])== 1){
				ConfigFlags.printResults = true;
			}else{
				ConfigFlags.printResults = false;
			}
		}

		if(args.length > 5){
			ConfigFlags.outFile = parrent_dir+args[5];
		}

		if(args.length > 6){
			eventtype = args[6].equalsIgnoreCase("kite")?"check":args[6];
			ConfigFlags.eventtype = eventtype.toLowerCase(Locale.ROOT);
		}

		ParseStockStreamConfig.parseStockEventConfig(streamConfigFile);
				
		StreamController myStreamController = null; 
		
		EngineController myEngineController = new EngineController();
		
		if(engineType != null){
			myEngineController = new EngineController(engineType);
		}
		myEngineController.setNfa(nfaFileLocation);


		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		myEngineController.initializeEngine();
		System.gc();
		myStreamController = new StreamController(StockStreamConfig.streamSize, eventtype+"Event");
		myStreamController.readStream(inputFile,eventtype);
		myEngineController.setInput(myStreamController.getMyStream());
		myStreamController.printStream();
		myEngineController.runEngine();
		Profiling.memoryUsed = runtime.totalMemory() - runtime.freeMemory();
		Profiling.printProfiling();
			
			
		}
}
