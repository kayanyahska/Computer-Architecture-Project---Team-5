public class STR extends AbstractInstruction {
	//	STR -> Store Register to Memory
	int r;
	int ix;
	int address;
	int i;

	@Override
	public void execute(String instruction, Registers registers, MCU mcu) throws MachineFaultException {

		r = StringUtil.binaryToDecimal(instruction.substring(6, 8));
		ix = StringUtil.binaryToDecimal(instruction.substring(8, 10));
		i = StringUtil.binaryToDecimal(instruction.substring(10, 11));
		address = StringUtil.binaryToDecimal(instruction.substring(11, 16));

		int effectiveAddress = EffectiveAddress.calculate(ix, address, i, mcu, registers);

		// reading the content of selected register using [R] in the instruction
		registers.setMAR(effectiveAddress);
		registers.setMBR(registers.getRnByNum(r));
		mcu.storeIntoCache(registers.getMAR(), registers.getMBR());
		registers.increasePCByOne();
	}

	@Override
	public String getExecuteMessage() {
		return "STR " + r + ", " + ix + ", " + address + ", " + i;
	}

}