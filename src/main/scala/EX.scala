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
      val WBSignal = Input(UInt(32.W))

    })

  // Drive the inputs along
  io.instructionOut := io.instructionIn
  io.PCOut := io.PCIn
  io.controlSignalsOut := io.controlSignalsIn
  io.writeData := io.registerData2
  io.jumpAddress := 0.U
  io.PCOverride := false.B

  // Set up and use the ALU
  val ALU = Module(new ALU()).io


  // Wire for alu register input
  val registerData1Forwarded = Wire(UInt(32.W))
  val registerData2Forwarded = Wire(UInt(32.W))

  registerData1Forwarded := io.registerData1
  registerData2Forwarded := io.registerData2

  ALU.aluOp := io.aluOp
  ALU.in1 := Mux(io.op1Select === Op1Select.rs1, registerData1Forwarded, 0.U)

  ALU.in2 := Mux(io.op2Select === Op2Select.rs2, registerData2Forwarded, io.instructionIn.getImmediate(io.immType).asUInt())
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

  when(io.controlSignalsIn.branch && io.instructionIn.opcode === "b1100011".U) {
    when(io.instructionIn.funct3 === "b0".U && ALU.zeroFlag) {    // BEQ
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    when(io.instructionIn.funct3 === "b1".U && ALU.zeroFlag === false.B) {    // BNE
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    when(io.instructionIn.funct3 === "b100".U && ALU.aluResult === 1.U) {   // BLT
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    when(io.instructionIn.funct3 === "b101".U && ALU.aluResult === 0.U) {   // BGE
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    when(io.instructionIn.funct3 === "b110".U && ALU.aluResult === 1.U) {   // BLTU
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    when(io.instructionIn.funct3 === "b111".U && ALU.aluResult === 0.U) {   // BGEU
      io.jumpAddress := (io.PCIn.asSInt() + io.instructionIn.immediateBType.pad(32)).asUInt()
      io.PCOverride := true.B
    }
    //printf(p"Instruction: ${io.instructionIn}\n")
  }

  // Forwarding decision unit
  when(FWRio.instructionMEM.registerRd === io.instructionIn.registerRs1) {
    registerData1Forwarded := FWRio.aluResultMEM
  }.elsewhen(FWRio.instructionWB.registerRd === io.instructionIn.registerRs1) {
    registerData1Forwarded := FWRio.WBSignal
  }

  when(FWRio.instructionMEM.registerRd === io.instructionIn.registerRs2) {
      registerData2Forwarded := FWRio.aluResultMEM
  }.elsewhen(FWRio.instructionWB.registerRd === io.instructionIn.registerRs2) {
    registerData2Forwarded := FWRio.WBSignal
  }

  dontTouch(io.aluResult)
  dontTouch(io.immType)









}