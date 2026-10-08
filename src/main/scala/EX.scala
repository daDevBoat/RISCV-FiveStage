package FiveStage
import chisel3._
import chisel3.util.{BitPat, MuxCase}
import chisel3.experimental.{MultiIOModule, dontTouch}


class Execute extends MultiIOModule {

  val io = IO(
    new Bundle {
      val instructionIn = Input(new Instruction())
      val PCIn = Input(UInt(32.W))
      val controlSignalsIn = Input(new ControlSignals())
      val branchType = Input(UInt(3.W))
      val op1Select = Input(UInt(1.W))
      val op2Select = Input(UInt(1.W))
      val immType = Input(UInt(3.W))
      val aluOp = Input(UInt(4.W))
      val registerData1 = Input(UInt(32.W))
      val registerData2 = Input(UInt(32.W))

      val instructionOut = Output(new Instruction())
      val PCOut = Output(UInt(32.W))
      val controlSignalsOut = Output(new ControlSignals())
      val aluResult = Output(UInt(32.W))
      val writeData = Output(UInt(32.W))
      val jumpAddress = Output(UInt(32.W))
      val PCOverride = Output(Bool())
    }
  )

  val FWRio = IO(
    new Bundle {
      val instructionMEM = Input(new Instruction())
      val aluResultMEM = Input(UInt(32.W))
      val instructionWB = Input(new Instruction())
      val WBData = Input(UInt(32.W))
      val memRead = Input(Bool())
      val memReadData = Input(UInt(32.W))

      val stallSignal = Output(Bool())

    })

  // Drive the inputs along
  io.instructionOut := io.instructionIn
  io.PCOut := io.PCIn
  io.controlSignalsOut := io.controlSignalsIn
  io.jumpAddress := 0.U
  io.PCOverride := false.B

  val ALU = Module(new ALU()).io
  val FWR = Module(new FWR()).io

  /* Forwarding logic */

  // Input signals into the FWR unit
  FWR.instructionEX := io.instructionIn
  FWR.instructionMEM := FWRio.instructionMEM
  FWR.instructionWB := FWRio.instructionWB
  FWR.aluResultMEM := FWRio.aluResultMEM
  FWR.WBData := FWRio.WBData
  FWR.registerData1In := io.registerData1
  FWR.registerData2In := io.registerData2
  FWR.memRead := FWRio.memRead
  FWR.memReadData := FWRio.memReadData

  // Drive the rs2 data from FWR to writeData
  io.writeData := FWR.registerData2Out

  // Drive the stallSignal
  FWRio.stallSignal := FWR.stallSignal

  // Take the output signals from the FWR unit and drive into the ALU
  ALU.aluOp := io.aluOp
  ALU.in1 := Mux(io.op1Select === Op1Select.rs1, FWR.registerData1Out, 0.U)

  ALU.in2 := Mux(io.op2Select === Op2Select.rs2, FWR.registerData2Out, io.instructionIn.getImmediate(io.immType).asUInt())
  io.aluResult := ALU.aluResult

  // Jump logic
  when(io.controlSignalsIn.jump) {
    io.aluResult := io.PCIn + 4.U   // Not technically an ALU result, but then I dont have to worry with another selection etc
    io.PCOverride := true.B

    when(io.instructionIn.opcode === "b1101111".U) {  // JAR
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateJType.pad(32)).asUInt()
    }.elsewhen(io.instructionIn.opcode === "b1100111".U) {  // JAL
      io.jumpAddress := (io.registerData1.asSInt() + io.instructionIn.immediateIType.pad(32)).asUInt() & "hfffffffe".U
    }
  }

  // Branch logic

  when(io.controlSignalsIn.branch) {
    when(io.branchType === branchType.beq && ALU.zeroFlag) {    // BEQ
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    when(io.branchType === branchType.neq && ALU.zeroFlag === false.B) {    // BNE
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    when(io.branchType === branchType.lt && ALU.aluResult === 1.U) {   // BLT
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    when(io.branchType === branchType.gte && ALU.aluResult === 0.U) {   // BGE
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    when(io.branchType === branchType.ltu && ALU.aluResult === 1.U) {   // BLTU
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    when(io.branchType === branchType.gteu && ALU.aluResult === 0.U) {   // BGEU
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    //printf(p"Instruction: ${io.instructionIn}\n")
  }

  dontTouch(io.aluResult)
  dontTouch(io.immType)









}