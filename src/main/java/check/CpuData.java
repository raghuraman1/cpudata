package check;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystem.ProcessFiltering;
import oshi.software.os.OperatingSystem.ProcessSorting;
import oshi.util.FormatUtil;

public class CpuData {

	public static void main(String[] args) {
		List<String> oshi = new ArrayList<>();

		  SystemInfo si = new SystemInfo();

	      HardwareAbstractionLayer hal = si.getHardware();
	      OperatingSystem os = si.getOperatingSystem();
	      CentralProcessor processor = hal.getProcessor();
	      GlobalMemory memory = hal.getMemory();
	      long[] prevTicks = processor.getSystemCpuLoadTicks();
	      long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
	      
	      oshi.add(String.format("CPU load: %.1f%%", processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100));
	        double[] loadAverage = processor.getSystemLoadAverage(3);
	        oshi.add("CPU load averages:" + (loadAverage[0] < 0 ? " N/A" : String.format(" %.2f", loadAverage[0]))
	                + (loadAverage[1] < 0 ? " N/A" : String.format(" %.2f", loadAverage[1]))
	                + (loadAverage[2] < 0 ? " N/A" : String.format(" %.2f", loadAverage[2])));
	        // per core CPU
	        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
	        double[] load = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
	        for (double avg : load) {
	            procCpu.append(String.format(" %.1f%%", avg * 100));
	        }
	        oshi.add(procCpu.toString());
	        
	        OSProcess myProc = os.getProcess(os.getProcessId());
	        // current process will never be null. Other code should check for null here
	        oshi.add(
	                "My PID: " + myProc.getProcessID() + " with affinity " + Long.toBinaryString(myProc.getAffinityMask()));
	        oshi.add("Processes: " + os.getProcessCount() + ", Threads: " + os.getThreadCount());
	        // Sort by highest CPU
	        List<OSProcess> procs = os.getProcesses(ProcessFiltering.ALL_PROCESSES, ProcessSorting.CPU_DESC, 5);
	        oshi.add("   PID  %CPU %MEM       VSZ       RSS Name");
	        for (int i = 0; i < procs.size(); i++) {
	            OSProcess p = procs.get(i);
	            oshi.add(String.format(" %5d %5.1f %4.1f %9s %9s %s", p.getProcessID(),
	                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
	                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
	                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName()));
	        }
	       
	        
	        for (Iterator iterator = oshi.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				System.out.println(string);
			}

	}

}
