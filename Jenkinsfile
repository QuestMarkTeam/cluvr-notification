pipeline {
    agent any

    environment {
        MODULE = 'cluvr-noti'
        AWS_REGION = 'us-west-2'
        ECR_REGISTRY = '617373894870.dkr.ecr.us-west-2.amazonaws.com'
        ECR_REPO = 'cluvr-noti'
        IMAGE_TAG = 'latest'
        EC2_IP = '54.218.142.135'
        ENV_PATH = '/home/ubuntu/.env'
    }

    stages {
        stage('Create .env & Send to EC2') {
            steps {
                echo '✅ Generating .env and sending to EC2...'
                withCredentials([
                    string(credentialsId: 'JWT_SECRET_KEY', variable: 'JWT_SECRET_KEY'),
                    string(credentialsId: 'REDIS_HOST', variable: 'REDIS_HOST'),
                    string(credentialsId: 'REDIS_PORT', variable: 'REDIS_PORT'),
                    string(credentialsId: 'RMQ_HOST', variable: 'RMQ_HOST'),
                    string(credentialsId: 'RMQ_PORT', variable: 'RMQ_PORT'),
                    string(credentialsId: 'RMQ_USERNAME', variable: 'RMQ_USERNAME'),
                    string(credentialsId: 'RMQ_PASSWORD', variable: 'RMQ_PASSWORD')
                ]) {
                    sh """
                        echo "JWT_SECRET_KEY=${JWT_SECRET_KEY}" > .env
                        echo "REDIS_HOST=${REDIS_HOST}" >> .env
                        echo "REDIS_PORT=${REDIS_PORT}" >> .env
                        echo "RMQ_HOST=${RMQ_HOST}" >> .env
                        echo "RMQ_PORT=${RMQ_PORT}" >> .env
                        echo "RMQ_USERNAME=${RMQ_USERNAME}" >> .env
                        echo "RMQ_PASSWORD=${RMQ_PASSWORD}" >> .env

                        scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/.ssh/id_rsa .env ubuntu@${EC2_IP}:${ENV_PATH}
                    """
                }
            }
        }

        stage('Checkout SCM') {
            steps {
                cleanWs()
                echo "✅ Checking out source code from GitHub..."
                checkout scm
            }
        }

        stage('Build & Deploy only if on develop branch') {
            when {
                branch 'develop'
            }

            steps {
                echo "✅ Deploying develop branch build..."

                script {
                    sh '''
                    docker build -t $ECR_REPO:$IMAGE_TAG .
                    docker tag $ECR_REPO:$IMAGE_TAG $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG
                    '''
                }

                script {
                    sh '''
                    aws ecr get-login-password --region $AWS_REGION \
                        | docker login --username AWS --password-stdin $ECR_REGISTRY
                    docker push $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG
                    '''
                }

                script {

                    // SSH 호스트 키 등록
                    sh '''
                    ssh-keyscan -H $EC2_IP >> ~/.ssh/known_hosts
                    '''

                    // Docker 네트워크 생성
                    sh '''
                    ssh -i /var/lib/jenkins/.ssh/id_rsa ubuntu@$EC2_IP "docker network create cluvr-net 2>/dev/null || echo 'Network already exists'"
                    '''

                    // RabbitMQ만 실행
                    sh '''
                    ssh -i /var/lib/jenkins/.ssh/id_rsa ubuntu@$EC2_IP '
                        echo "🔍 RabbitMQ 상태 확인 중..."
                        if [ -z "$(docker ps -q -f name=rabbitmq)" ]; then
                            echo "📦 RabbitMQ 시작 중..."
                            docker run -d --name rabbitmq --network cluvr-net -p 5672:5672 -p 15672:15672 --restart unless-stopped \
                                -e RABBITMQ_DEFAULT_USER=${RMQ_USERNAME} \
                                -e RABBITMQ_DEFAULT_PASS=${RMQ_PASSWORD} \
                                rabbitmq:3-management
                        else
                            echo "✅ RabbitMQ 이미 실행 중 - 스킵"
                        fi
                    '
                    '''

                    // 기존 앱 중지 및 제거
                    sh '''
                    ssh -i /var/lib/jenkins/.ssh/id_rsa ubuntu@$EC2_IP "
                        docker stop $ECR_REPO 2>/dev/null || true
                        docker rm $ECR_REPO 2>/dev/null || true
                    "
                    '''

                    // 최신 이미지 pull
                    sh '''
                    ssh -i /var/lib/jenkins/.ssh/id_rsa ubuntu@$EC2_IP "
                        aws ecr get-login-password --region $AWS_REGION \
                            | docker login --username AWS --password-stdin $ECR_REGISTRY
                        docker pull $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG
                    "
                    '''

                    // 앱 실행
                    sh '''
                    ssh -i /var/lib/jenkins/.ssh/id_rsa ubuntu@$EC2_IP "
                        docker run -d --name $ECR_REPO \
                            --network host \
                            --env-file ${ENV_PATH} \
                            --log-driver json-file \
                            --log-opt max-size=10m \
                            --log-opt max-file=3 \
                            --restart unless-stopped \
                            $ECR_REGISTRY/$ECR_REPO:$IMAGE_TAG

                        echo '🎉 배포 완료! 앱이 실행 중입니다.'
                        echo '📍 접속 주소: http://$EC2_IP'
                    "
                    '''
                }
            }
        }
    }
}
