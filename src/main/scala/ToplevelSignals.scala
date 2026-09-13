package FiveStage
import Chisel.MuxLookup
import chisel3._
import chisel3.core.Wire
import chisel3.util.{BitPat, Cat, is, switch}


class Instruction extends Bundle(){
  val instruction = UInt(32.W)

  def opcode      = instruction(6, 0)
  def registerRd  = instruction(11, 7)
  def funct3      = instruction(14, 12)
  def registerRs1 = instruction(19, 15)
  def registerRs2 = instruction(24, 20)
  def funct7      = instruction(31, 25)
  def funct6      = instruction(26, 31)

  def immediateIType = instruction(31, 20).asSInt
  def immediateSType = Cat(instruction(31, 25), instruction(11,7)).asSInt
  def immediateBType = Cat(instruction(31), instruction(7), instruction(30, 25), instruction(11, 8), 0.U(1.W)).asSInt
  def immediateUType = Cat(instruction(31, 12), 0.U(12.W)).asSInt
  def immediateJType = Cat(instruction(31), instruction(19, 12), instruction(20), instruction(30, 25), instruction(24, 21), 0.U(1.W)).asSInt
  def immediateZType = instruction(19, 15).zext
  def immediateShamtType = instruction(24, 20).asSInt()

  def bubble(): Instruction = {
    val bubbled = Wire(new Instruction)
    bubbled.instruction := instruction
    bubbled.instruction(6, 0) := BitPat.bitPatToUInt(BitPat("b0010011"))
    bubbled
  }

  def getImmediate(immFormat: UInt): SInt = {
    MuxLookup(immFormat, 0.S(32.W), Seq(
      ImmFormat.ITYPE -> immediateIType,
      ImmFormat.STYPE -> immediateSType,
      ImmFormat.BTYPE -> immediateBType,
      ImmFormat.UTYPE -> immediateUType,
      ImmFormat.JTYPE -> immediateJType,
      ImmFormat.SHAMT -> immediateShamtType
    ))
  }

}

object InstrType {
  // R-type
  val ADD  = 0.U(5.W)
  val SUB  = 1.U(5.W)
  val AND  = 2.U(5.W)
  val OR   = 3.U(5.W)
  val XOR  = 4.U(5.W)
  val SLT  = 5.U(5.W)
  val SLTU = 6.U(5.W)
  val SRA  = 7.U(5.W)
  val SRL  = 8.U(5.W)
  val SLL  = 9.U(5.W)

  // I-type arithmetic
  val ADDI  = 10.U(5.W)
  val ANDI  = 11.U(5.W)
  val ORI   = 12.U(5.W)
  val XORI  = 13.U(5.W)
  val SLTI  = 14.U(5.W)
  val SLTIU = 15.U(5.W)
  val SRAI  = 16.U(5.W)
  val SRLI  = 17.U(5.W)
  val SLLI  = 18.U(5.W)

  // U-type
  val LUI   = 19.U(5.W)
  val AUIPC = 20.U(5.W)

  // Memory
  val LW = 21.U(5.W)
  val SW = 22.U(5.W)

  val NOP = 31.U(5.W)
}


object Instruction {
  def NOP: Instruction = {
    val w = Wire(new Instruction)
    w.instruction := BitPat.bitPatToUInt(BitPat("b00000000000000000000000000010011"))
    w
  }
}


class ControlSignals extends Bundle(){
  val regWrite   = Bool()
  val memRead    = Bool()
  val memWrite   = Bool()
  val branch     = Bool()
  val jump       = Bool()
}


object ControlSignals {
  def nop: ControlSignals = {
    val b = Wire(new ControlSignals)
    b.regWrite   := false.B
    b.memRead    := false.B
    b.memWrite   := false.B
    b.branch     := false.B
    b.jump       := false.B
    b
  }
}


object branchType {
  val beq  = 0.asUInt(3.W)
  val neq  = 1.asUInt(3.W)
  val gte  = 2.asUInt(3.W)
  val lt   = 3.asUInt(3.W)
  val gteu = 4.asUInt(3.W)
  val ltu  = 5.asUInt(3.W)
  val jump = 6.asUInt(3.W)
  val DC   = 7.asUInt(3.W)
}


/**
  these take the role of the alu source signal.
  Used in the decoder.
  In the solution manual I use these to select signals at the decode stage.
  You can choose to instead do this in the execute stage, and you may forego
  using them altogether.
  */
object Op1Select {
  val rs1 = 0.asUInt(1.W)
  val PC  = 1.asUInt(1.W)
  val DCOp1  = 0.asUInt(1.W)
}

object Op2Select {
  val rs2 = 0.asUInt(1.W)
  val imm = 1.asUInt(1.W)
  val DCOp2  = 0.asUInt(1.W)
}


/**
  Used in the decoder
  */
object ImmFormat {
  val ITYPE  = 0.asUInt(3.W)
  val STYPE  = 1.asUInt(3.W)
  val BTYPE  = 2.asUInt(3.W)
  val UTYPE  = 3.asUInt(3.W)
  val JTYPE  = 4.asUInt(3.W)
  val SHAMT  = 5.asUInt(3.W)
  val DC     = 0.asUInt(3.W)
}


object ALUOps {
  val ADD    = 0.U(4.W)
  val SUB    = 1.U(4.W)
  val AND    = 2.U(4.W)
  val OR     = 3.U(4.W)
  val XOR    = 4.U(4.W)
  val SLT    = 5.U(4.W)
  val SLL    = 6.U(4.W)
  val SLTU   = 7.U(4.W)
  val SRL    = 8.U(4.W)
  val SRA    = 9.U(4.W)
  val COPY_A = 10.U(4.W)
  val COPY_B = 11.U(4.W)

  val DC     = 15.U(4.W)
}
