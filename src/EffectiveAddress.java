public class EffectiveAddress {

	public static int calculate(int ix, int address, int i, MCU mcu, Registers registers)
			throws MachineFaultException {
		if (i == 1) {
			// indirect addressing
			if (ix == 0) {// No indexing
				if (checkMachineFault(address, mcu) == 1) {
					registers.setMAR(address);
					registers.setMBR(mcu.fetchFromCache(registers.getMAR()));
				}
			} else {
				if (checkMachineFault(address + registers.getXnByNum(ix), mcu) == 1) {
					registers.setMAR(address + registers.getXnByNum(ix));
					registers.setMBR(mcu.fetchFromCache(registers.getMAR()));
				}
			}
			return registers.getMBR();
		}

		if (i == 0) {
			// No indirect addressing
			if (ix == 0) {// No indexing
				if (checkMachineFault(address, mcu) == 1) {
					return address;
				}
			} else {
				if (checkMachineFault(address + registers.getXnByNum(ix), mcu) == 1) {
					return address + registers.getXnByNum(ix);
				}
			}

		}

		return 0;
	}

	// checking for machine fault
	public static int checkMachineFault(int address, MCU mcu) throws MachineFaultException {
		if (address > mcu.getCurrentMemorySize() - 1) {
			throw new MachineFaultException(Constants.FaultCode.ILL_MEM_BYD.getValue(),
					Constants.FaultCode.ILL_MEM_BYD.getMessage());
		} else {
			return 1;
		}
	}

}